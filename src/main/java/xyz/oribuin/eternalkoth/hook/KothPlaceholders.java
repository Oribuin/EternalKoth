package xyz.oribuin.eternalkoth.hook;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.utils.HexUtils;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.oribuin.eternalkoth.koth.Zone;
import xyz.oribuin.eternalkoth.manager.ConfigurationManager.Setting;
import xyz.oribuin.eternalkoth.manager.KothManager;
import xyz.oribuin.eternalkoth.util.KothUtils;

public class KothPlaceholders extends PlaceholderExpansion {

    private final KothManager manager;

    public KothPlaceholders(RosePlugin rosePlugin) {
        this.manager = rosePlugin.getManager(KothManager.class);
    }

    @Override
    public @Nullable String onRequest(OfflinePlayer player, @NotNull String params) {
        Zone activeZone = this.manager.getActiveZone();

        return switch (params.toLowerCase()) {
            case "status" -> activeZone != null ? "Active" : "Inactive";
            case "current" -> activeZone != null ? activeZone.getId() : "None";
            case "total" -> KothUtils.convertMillis(activeZone != null ? activeZone.getTimeToCapture().toMillis() : 0);
            case "duration" -> KothUtils.convertMillis(activeZone != null ? activeZone.getMaxDuration().toMillis() : 0);
            case "timeleft" -> KothUtils.convertMillis(activeZone != null ? activeZone.getRemainingTime() : 0);
            case "progress" -> KothUtils.convertMillis(activeZone != null ? activeZone.getCaptureElapsed() : 0);
            case "bar" -> this.getProgressBar(activeZone);
            case "captain" -> {
                if (activeZone == null || activeZone.getCaptain() == null || activeZone.getCaptainName() == null) {
                    yield "None";
                }

                yield activeZone.getCaptainName();
            }

            default -> null;
        };
    }

    /**
     * Get the progress bar for the zone
     *
     * @param zone The zone
     * @return The progress bar
     */
    private String getProgressBar(Zone zone) {
        int barLength = Setting.BAR_LENGTH.getInt();
        String barChar = Setting.BAR_CHAR.getString();

        if (zone == null) return HexUtils.colorify("&c" + barChar.repeat(barLength));
        double progressPercent = zone.getProgressPercent();
        int progress = (int) (progressPercent * barLength);

        if (progress > barLength) progress = barLength;
        if (progress < 0) progress = 0;

        return HexUtils.colorify("&a" + barChar.repeat(progress) + "&c" + barChar.repeat(barLength - progress));
    }

    @Override
    public @NotNull String getIdentifier() {
        return "eternalkoth";
    }

    @Override
    public @NotNull String getAuthor() {
        return "Oribuin";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0.0";
    }

    @Override
    public boolean persist() {
        return true;
    }

}
