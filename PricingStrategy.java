public enum PricingStrategy {
    BEST {
        @Override
        public double calculate(List<Tick> ticks, int lookback) {
            return ticks.stream()
                    .mapToDouble(Tick::getPrice)
                    .min()
                    .orElseThrow(() -> new IllegalArgumentException("No ticks found"));
        }
    },
    WORST {
        @Override
        public double calculate(List<Tick> ticks, int lookback) {
            return ticks.stream()
                    .mapToDouble(Tick::getPrice)
                    .max()
                    .orElseThrow(() -> new IllegalArgumentException("No ticks found"));
        }
    },
    VWAP {
        @Override
        public double calculate(List<Tick> ticks, int lookback) {
            double totalValue = ticks.stream().mapToDouble(t -> t.getPrice() * t.getVolume()).sum();
            double totalVolume = ticks.stream().mapToDouble(Tick::getVolume).sum();
            return totalValue / totalVolume;
        }
    },
    DEEP_AVG {
        @Override
        public double calculate(List<Tick> ticks, int lookback) {
            List<Double> worstPrices = ticks.stream()
                    .map(Tick::getPrice)
                    .sorted(Comparator.reverseOrder()) // worst = highest
                    .limit(lookback)
                    .collect(Collectors.toList());
            return worstPrices.stream().mapToDouble(Double::doubleValue).average()
                    .orElseThrow(() -> new IllegalArgumentException("Not enough ticks for Deep Avg"));
        }
    },
    DEEP_VWAP {
        @Override
        public double calculate(List<Tick> ticks, int lookback) {
            List<Tick> worstTicks = ticks.stream()
                    .sorted(Comparator.comparingDouble(Tick::getPrice).reversed())
                    .limit(lookback)
                    .collect(Collectors.toList());

            double totalValue = worstTicks.stream().mapToDouble(t -> t.getPrice() * t.getVolume()).sum();
            double totalVolume = worstTicks.stream().mapToDouble(Tick::getVolume).sum();
            return totalValue / totalVolume;
        }
    };

    public abstract double calculate(List<Tick> ticks, int lookback);
}
