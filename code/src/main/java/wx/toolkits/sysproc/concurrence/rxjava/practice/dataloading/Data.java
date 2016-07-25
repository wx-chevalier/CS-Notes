package wx.toolkits.sysproc.concurrence.rxjava.practice.dataloading;

/**
 * Created by apple on 16/4/21.
 */

/**
 * Simple data class, keeps track of when it was created
 * so that it knows when the its gone stale.
 */
public class Data {

    private static final long STALE_MS = 5 * 1000; // Data is stale after 5 seconds

    final String value;

    final long timestamp;

    public Data(String value) {
        this.value = value;
        this.timestamp = System.currentTimeMillis();
    }

    public boolean isUpToDate() {
        return System.currentTimeMillis() - timestamp < STALE_MS;
    }
}
