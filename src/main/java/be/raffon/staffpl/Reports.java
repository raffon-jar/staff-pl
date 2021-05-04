package be.raffon.staffpl;

import be.raffon.staffpl.inventories.CItem;

import java.nio.charset.Charset;
import java.sql.Array;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

public class Reports {
    public ArrayList<Report> reports;

    public Reports() {
        getFromDB();
    }

    public void addReport(Report report) {
        reports.add(report);
    }

    public void getFromDB() {
        AtomicReference<ArrayList<Report>> arr = new AtomicReference<ArrayList<Report>>();
        SQLManager.getInstance().query(" SELECT * FROM staff_reports;", re -> {
            try {
                ArrayList<Report> reports = new ArrayList<Report>();
                while (re.next()) {
                    String random = new RandomString(10, new Random()).nextString();
                    reports.add(new Report(re.getTimestamp("date"), re.getString("reason"), UUID.fromString(re.getString("reporter")), UUID.fromString(re.getString("player")), re.getInt("status"), random));
                }
                arr.set(reports);
            } catch (SQLException e) {

            }
        });
        reports = arr.get();
    }

    public ArrayList<Report> getFromPlayer(UUID pl) {
        ArrayList<Report> reports = new ArrayList<Report>();
        for(int i=0; i<reports.size(); i++) {
            Report report = reports.get(i);
            if(report.player.equals(pl)) {
                reports.add(report);
            }
        }
        return reports;
    }



    public void storetoDB() {
        for(int i=0; i<reports.size() ;i++) {
            Report report = reports.get(i);

            System.out.println("INSERT INTO staff_reports (date, reason, reporter, player, status) VALUES (CURRENT_TIMESTAMP(), '" + report.reason + "', '" + report.reporter + "', '" + report.player + "', "+ report.status + ") " +
                    "ON DUPLICATE KEY UPDATE id=id");
            SQLManager.getInstance().update("INSERT INTO staff_reports (date, reason, reporter, player, status) VALUES (CURRENT_TIMESTAMP(), '" + report.reason + "', '" + report.reporter + "', '" + report.player + "', "+ report.status + ") " +
                    "ON DUPLICATE KEY UPDATE id=id");
        }
    }

    public void deleteKey(String key) {
        for(int i=0; i<reports.size(); i++) {
            Report report = reports.get(i);
            if(report.key.equals(key)) {
                reports.remove(i);
            }
        }
    }
}
