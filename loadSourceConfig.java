private Map<String, PricingStrategy> loadSourceConfig() {
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
