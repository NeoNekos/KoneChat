package cc.yamrc.konechat.channel;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;

import java.util.Optional;

public final class PlayerChannelData {
    private static final String ROOT = "konechat";
    private static final String ACTIVE = "ActiveChannel";

    private PlayerChannelData() {
    }

    public static Optional<String> get(Player player) {
        CompoundTag persisted = player.getPersistentData().getCompound(Player.PERSISTED_NBT_TAG);
        CompoundTag root = persisted.getCompound(ROOT);
        return root.contains(ACTIVE) ? Optional.of(root.getString(ACTIVE)) : Optional.empty();
    }

    public static void set(Player player, String id) {
        CompoundTag persisted = player.getPersistentData().getCompound(Player.PERSISTED_NBT_TAG);
        CompoundTag root = persisted.getCompound(ROOT);
        if (id == null) root.remove(ACTIVE);
        else root.putString(ACTIVE, id);
        persisted.put(ROOT, root);
        player.getPersistentData().put(Player.PERSISTED_NBT_TAG, persisted);
    }
}
