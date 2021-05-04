package be.raffon.staffpl;

import java.util.UUID;

public class Pstats {

    public UUID player;
    public Integer diamonds;
    public Integer iron;
    public Integer Coals;
    public Integer golds;
    public Integer netherite;
    public Integer normal_blocks;
    public Integer normal_nether;

    public Pstats(UUID player, Integer diamonds, Integer Coals, Integer Iron, Integer golds, Integer netherite, Integer normal_blocks, Integer normal_nether) {
        this.player = player;
        this.diamonds = diamonds;
        this.Coals = Coals;
        this.iron = Iron;
        this.golds = golds;
        this.netherite = netherite;
        this.normal_blocks = normal_blocks;
        this.normal_nether = normal_nether;
    }
}
