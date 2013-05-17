package SummonSpider;

import com.herocraftonline.heroes.Heroes;
import com.herocraftonline.heroes.api.SkillResult;
import com.herocraftonline.heroes.characters.Hero;
import com.herocraftonline.heroes.characters.skill.*;
import com.herocraftonline.heroes.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTargetEvent;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: nicolaslachance
 * Date: 2013-05-03
 * Time: 10:26 AM
 * To change this template use File | Settings | File Templates.
 */
public class SummonSpiders extends TargettedSkill
{
    private Heroes plugin;
    private String applyText;
    private String expireText;
    Spider spid;
    Player player;
    Map<Integer,String> map = new HashMap<Integer,String>();



    public SummonSpiders(Heroes plugin)
    {
        super(plugin, "SummonSpider");
        setDescription("100% chance to spawn 1 spider, $1% for 2, and $2% for 3.");
        setUsage("/skill spiders <target>");
        setArgumentRange(0, 0);
        setIdentifiers(new String[] { "skill spider", "skill spider" });
        setTypes(new SkillType[] { SkillType.SUMMON, SkillType.SILENCABLE, SkillType.EARTH });
        Bukkit.getServer().getPluginManager().registerEvents(new SkillHeroListener(this), plugin);
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
        node.set(SkillSetting.APPLY_TEXT.node(), "%target% is being attack by a spider!");
        return node;
    }

    public void init()
    {
        super.init();
        this.applyText = SkillConfigManager.getRaw(this, SkillSetting.APPLY_TEXT.node(), "%target% is being attack by a spider!").replace("%target%", "$1");
    }

    public SkillResult use(Hero hero, LivingEntity target, String[] args)
    {
        player = hero.getPlayer();
        broadcastExecuteText(hero);
        double chance2x = SkillConfigManager.getUseSetting(hero, this, "chance-2x", 0.2D, false) + (int)SkillConfigManager.getUseSetting(hero, this, "chance-2x-per-level", 0.0D, false) * hero.getSkillLevel(this);
        double chance3x = SkillConfigManager.getUseSetting(hero, this, "chance-3x", 0.1D, false) + (int)SkillConfigManager.getUseSetting(hero, this, "chance-3x-per-level", 0.0D, false) * hero.getSkillLevel(this);
        int distance = SkillConfigManager.getUseSetting(hero, this, SkillSetting.MAX_DISTANCE, 20, false) + (int)SkillConfigManager.getUseSetting(hero, this, SkillSetting.MAX_DISTANCE_INCREASE, 0.0D, false) * hero.getSkillLevel(this);
        Block wTargetBlock = player.getTargetBlock(null, distance).getRelative(BlockFace.UP);


        if(target !=null)
        {
            spid = (CaveSpider)player.getWorld().spawnEntity(wTargetBlock.getLocation(), EntityType.CAVE_SPIDER);
            map.put(spid.getEntityId(),target.getCustomName());
            targetEnemy(spid, target, player);


            double chance = Util.nextRand();

            if (chance <= chance3x)
            {
                spid = (CaveSpider)player.getWorld().spawnEntity(wTargetBlock.getLocation(), EntityType.CAVE_SPIDER);
                map.put(spid.getEntityId(),target.getCustomName());
                //targetEnemy(spid, target, player);
                spid = (CaveSpider)player.getWorld().spawnEntity(wTargetBlock.getLocation(), EntityType.CAVE_SPIDER);
                map.put(spid.getEntityId(),target.getCustomName());
                //targetEnemy(spid, target, player);
            } else if (chance <= chance2x)
            {
                spid = (CaveSpider)player.getWorld().spawnEntity(wTargetBlock.getLocation(), EntityType.CAVE_SPIDER);
                map.put(spid.getEntityId(),target.getCustomName());
                //targetEnemy(spid, target, player);
            }

            broadcastExecuteText(hero);
            return SkillResult.NORMAL;

        }

       return SkillResult.INVALID_TARGET;
    }

    public void targetEnemy(Spider ent, LivingEntity tar, Player summoner)
    {
        if(tar != summoner)
        {
            ent.setTarget(tar);
        }
    }
    public class SkillHeroListener implements Listener
    {

        private SummonSpiders top;

        public SkillHeroListener(SummonSpiders top)
        {
            this.top = top;
        }

        @EventHandler
        public void onEntityTarget(EntityTargetEvent event)
        {
            if(event.getEntityType().equals(EntityType.CAVE_SPIDER) && event.getEntity().equals(top.spid) && top.map.containsKey(event.getEntity().getEntityId()))
            {
                if(top.player.equals(event.getTarget()))
                {
                    event.setCancelled(true);
                }
                else
                {
                    top.targetEnemy((CaveSpider)event.getEntity(), Bukkit.getServer().getPlayer(top.map.get(event.getEntity().getEntityId())), top.player);
                }
            }
        }
        }

}
