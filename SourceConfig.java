import com.fasterxml.jackson.databind.ObjectMapper;

public class SourceConfig {
    private String strategy;
    private int lookback;

    public String getStrategy() { return strategy; }
    public int getLookback() { return lookback; }
}
