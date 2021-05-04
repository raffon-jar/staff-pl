package be.raffon.staffpl;

import java.sql.Timestamp;
import java.util.UUID;

public class Report {
    public Timestamp date;
    public String reason;
    public UUID reporter;
    public UUID player;
    public Integer status;
    public String key;

    public Report(Timestamp date, String reason, UUID reporter, UUID player, Integer status, String key) {
        this.date = date;
        this.reason = reason;
        this.reporter = reporter;
        this.player = player;
        this.status = status;
        this.key = key;
    }
}
