package be.raffon.staffpl.utils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

public class Baltop {

    public ArrayList<Pstats> pstats;

    public Baltop() {
        getFromDB();
    }

    public void addStats(Pstats stats) {
        pstats.add(stats);
    }

    public void getFromDB() {
        AtomicReference<ArrayList<Pstats>> arr = new AtomicReference<ArrayList<Pstats>>();
        SQLManager.getInstance().query(" SELECT * FROM staff_baltop;", re -> {
            try {
                ArrayList<Pstats> baltop = new ArrayList<Pstats>();
                while (re.next()) {
                    baltop.add(new Pstats(UUID.fromString(re.getString("username")), re.getInt("diamonds"), re.getInt("coal"), re.getInt("iron"), re.getInt("golds"), re.getInt("netherite"), re.getInt("normal_blocks"), re.getInt("normal_nether")));
                }
                arr.set(baltop);
            } catch (SQLException e) {

            }
        });
        pstats = arr.get();
    }

    public Pstats getFromPlayer(UUID pl) {
        Pstats sta = null;
        for(int i=0; i<this.pstats.size(); i++) {
            Pstats pstat = pstats.get(i);
            if(pstat.player.equals(pl)) {
                sta = pstat;
            }
        }

        if(sta == null) {
            sta = new Pstats(pl, 0, 0, 0, 0,0,0,1);
            pstats.add(sta);
        }
        return sta;
    }

    public void updateStats(Pstats stats, UUID pl) {
        Pstats Pstats = null;
        for(int i=0; i<this.pstats.size(); i++) {
            Pstats pstat = pstats.get(i);
            if(pstat.player.equals(pl)) {
                this.pstats.remove(i);
                this.pstats.add(stats);
            }
        }
    }

    public void storetoDB() {
        for(int i=0; i<pstats.size() ;i++) {
            Pstats stats = pstats.get(i);
            SQLManager.getInstance().update("INSERT INTO staff_baltop (username, diamonds, coal, iron, golds, netherite, normal_blocks, normal_nether) VALUES ('" + stats.player + "', " + stats.diamonds + ", " + stats.Coals + ", " + stats.iron + ", "+ stats.golds + ", " + stats.netherite + ", " + stats.normal_blocks + ", "+stats.normal_nether + ") " +
                    "ON DUPLICATE KEY UPDATE diamonds="+stats.diamonds+", coal=" + stats.Coals + ",iron="+stats.iron+",golds="+stats.golds+",netherite="+stats.netherite+",normal_blocks="+stats.normal_blocks+",normal_nether="+stats.normal_nether);
        }
    }
}
