import redis.clients.jedis.Jedis;

import java.util.*;

public class PriceScheduler {

    private static final Map<String, PricingStrategy> sourceConfig = Map.of(
            "P1", PricingStrategy.BEST,
            "P2", PricingStrategy.DEEP_AVG,
            "P3", PricingStrategy.VWAP
    );

    private Jedis redisClient = new Jedis("localhost", 6379);

    public void runScheduler() {
        Set<String> keys = redisClient.keys("tickbuf:*");
        Map<String, List<Tick>> groupedTicks = new HashMap<>();

        for (String key : keys) {
            // Example: tickbuf:P1:USD.INR:SPOT:10000.0
            String[] parts = key.split(":");
            String source = parts[1];
            String currency = parts[2];
            String type = parts[3];
            double volume = Double.parseDouble(parts[4]);

            // Dummy price fetch from Redis (replace with actual HGET or GET)
            double price = Double.parseDouble(redisClient.get(key));

            Tick tick = new Tick(price, volume);
            String groupKey = source + ":" + currency + ":" + type + ":" + volume;

            groupedTicks.computeIfAbsent(groupKey, k -> new ArrayList<>()).add(tick);
        }

        // Apply strategy per source
        groupedTicks.forEach((group, ticks) -> {
            String source = group.split(":")[0];
            PricingStrategy strategy = sourceConfig.getOrDefault(source, PricingStrategy.BEST);

            double finalPrice = strategy.calculate(ticks);
            System.out.println("Group: " + group + " => " + finalPrice + " (Strategy: " + strategy + ")");
        });
    }
    private Map<String, PricingStrategy> loadSourceStrategies() {
    Map<String, PricingStrategy> sourceConfig = new HashMap<>();
    Map<String, String> redisConfig = redisClient.hgetAll("pricing:strategy");

    redisConfig.forEach((source, strategyName) -> {
        try {
            PricingStrategy strategy = PricingStrategy.valueOf(strategyName.toUpperCase());
            sourceConfig.put(source, strategy);
        } catch (IllegalArgumentException e) {
            System.err.println("Invalid strategy in Redis for " + source + ": " + strategyName);
        }
    });

    return sourceConfig;
}

private Map<String, Integer> loadSourceLookbacks() {
    Map<String, Integer> lookbacks = new HashMap<>();
    Map<String, String> redisLookback = redisClient.hgetAll("pricing:lookback");

    redisLookback.forEach((source, value) -> {
        try {
            lookbacks.put(source, Integer.parseInt(value));
        } catch (NumberFormatException e) {
            System.err.println("Invalid lookback in Redis for " + source + ": " + value);
        }
    });

    return lookbacks;
}

}
