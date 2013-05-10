package SummonSpider;

import com.herocraftonline.heroes.Heroes;
import com.herocraftonline.heroes.api.SkillResult;
import com.herocraftonline.heroes.characters.Hero;
import com.herocraftonline.heroes.characters.skill.ActiveSkill;
import com.herocraftonline.heroes.characters.skill.SkillConfigManager;
import com.herocraftonline.heroes.characters.skill.SkillSetting;
import com.herocraftonline.heroes.characters.skill.SkillType;
import com.herocraftonline.heroes.util.Util;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

/**
 * Created with IntelliJ IDEA.
 * User: nicolaslachance
 * Date: 2013-05-03
 * Time: 10:26 AM
 * To change this template use File | Settings | File Templates.
 */
public class SummonSpiders extends ActiveSkill
{
    public SummonSpiders(Heroes plugin)
    {
        super(plugin, "SummonSpider");
        setDescription("100% chance to spawn 1 spider, $1% for 2, and $2% for 3.");
        setUsage("/skill spider");
        setArgumentRange(0, 0);
        setIdentifiers(new String[] { "skill SummonSpider", "skill spider" });
        setTypes(new SkillType[] { SkillType.SUMMON, SkillType.SILENCABLE, SkillType.EARTH });
    }

    public String getDescription(Hero hero)
    {
        int chance2x = (int)(SkillConfigManager.getUseSetting(hero, this, "chance-2x", 0.2D, false) * 100.0D + SkillConfigManager.getUseSetting(hero, this, "chance-2x-per-level", 0.0D, false) * hero.getLevel());
        int chance3x = (int)(SkillConfigManager.getUseSetting(hero, this, "chance-3x", 0.1D, false) * 100.0D + SkillConfigManager.getUseSetting(hero, this, "chance-3x-per-level", 0.0D, false) * hero.getLevel());
        return getDescription().replace("$2", chance2x + "").replace("$3", chance3x + "");
    }

    public ConfigurationSection getDefaultConfig()
    {
        ConfigurationSection node = super.getDefaultConfig();
        node.set("chance-2x", Double.valueOf(0.2D));
        node.set("chance-3x", Double.valueOf(0.1D));
        node.set(SkillSetting.MAX_DISTANCE.node(), Integer.valueOf(20));
        node.set("chance-2x-per-level", Double.valueOf(0.0D));
        node.set("chance-3x-per-level", Double.valueOf(0.0D));
        return node;
    }

    public SkillResult use(Hero hero, String[] args)
    {
        Player player = hero.getPlayer();
        broadcastExecuteText(hero);
        double chance2x = SkillConfigManager.getUseSetting(hero, this, "chance-2x", 0.2D, false) + (int)SkillConfigManager.getUseSetting(hero, this, "chance-2x-per-level", 0.0D, false) * hero.getSkillLevel(this);
        double chance3x = SkillConfigManager.getUseSetting(hero, this, "chance-3x", 0.1D, false) + (int)SkillConfigManager.getUseSetting(hero, this, "chance-3x-per-level", 0.0D, false) * hero.getSkillLevel(this);
        int distance = SkillConfigManager.getUseSetting(hero, this, SkillSetting.MAX_DISTANCE, 20, false) + (int)SkillConfigManager.getUseSetting(hero, this, SkillSetting.MAX_DISTANCE_INCREASE, 0.0D, false) * hero.getSkillLevel(this);
        Block wTargetBlock = player.getTargetBlock(null, distance).getRelative(BlockFace.UP);
        player.getWorld().spawnEntity(wTargetBlock.getLocation(), EntityType.CAVE_SPIDER);
        double chance = Util.nextRand();
        if (chance <= chance3x) {
            player.getWorld().spawnEntity(wTargetBlock.getLocation(), EntityType.CAVE_SPIDER);
            player.getWorld().spawnEntity(wTargetBlock.getLocation(), EntityType.CAVE_SPIDER);
        } else if (chance <= chance2x) {
            player.getWorld().spawnEntity(wTargetBlock.getLocation(), EntityType.CAVE_SPIDER);
        }
        broadcastExecuteText(hero);
        return SkillResult.NORMAL;
    }
}
