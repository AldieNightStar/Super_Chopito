package haxidenti.schopito;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.util.HashMap;

public class Main extends JavaPlugin implements Listener {

    private String prefix = ChatColor.YELLOW + "SuperChopito AXE";
    private int level = 10;
    private HashMap<String, Long> periods = new HashMap<>(64);

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
        FileConfiguration config = getConfig();
        level = config.getInt("minimum_level", 10);
        prefix = ChatColor.RED + config.getString("prefix_name", "SuperChopito AXE");
    }

    @Override
    public void onDisable() {
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!command.getName().equalsIgnoreCase("super_chopito")) return true;
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only for players!");
            return true;
        }
        Player player = (Player) sender;
        PlayerInventory inventory = player.getInventory();
        ItemStack itemInHand = inventory.getItemInMainHand();

        if (!itemInHand.getType().equals(Material.STICK) || itemInHand.getAmount() < 16) {
            player.sendMessage(ChatColor.RED + "You need to have 16 sticks in your hand to do it");
            return true;
        }
        itemInHand.setAmount(itemInHand.getAmount() - 16);

        ItemStack item = new ItemStack(Material.WOODEN_AXE);
        item.setDisplayName(prefix);
        {
            ItemMeta meta = item.getItemMeta();
            meta.addEnchant(Enchantment.BINDING_CURSE, 1, true);
            item.setItemMeta(meta);
        }
        player.getWorld().dropItem(player.getLocation(), item);
        return true;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        Action action = e.getAction();
        if (!action.equals(Action.RIGHT_CLICK_BLOCK)) return;

        Player player = e.getPlayer();

        Long period = periods.get(player.getName());
        if (period != null && period > 0) {
            long now = Instant.now().toEpochMilli();
            if (period > now) {
                return;
            }
        }

        PlayerInventory inventory = player.getInventory();
        ItemStack itemInHand = inventory.getItemInMainHand();
        Block interactBlock = e.getClickedBlock();

        if (!itemInHand.getType().equals(Material.WOODEN_AXE)) return;
        if (!itemInHand.getDisplayName().equals(prefix)) return;
        if (interactBlock == null) return;
        if (player.getLevel() < level) {
            player.sendMessage(ChatColor.RED + "Your level is too small. Need" + level + " XP Level to do it!");
            return;
        }

        Location interactLocation = interactBlock.getLocation().add(0, 1, 0);

        SuperChopitoAPI.superChopAt(interactLocation, this);
        player.playSound(player.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1f, 1f);
        player.setLevel(player.getLevel() - level);

        periods.put(player.getName(), Instant.now().toEpochMilli() + 1000);
    }
}
