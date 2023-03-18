package bot.inker.ankh.core.world.entity;

import bot.inker.ankh.core.common.entity.LocationEmbedded;
import bot.inker.ankh.core.world.storage.BlockStorageEntry;
import jakarta.persistence.*;
import org.bukkit.NamespacedKey;

import javax.annotation.Nonnull;
import java.util.Objects;

@Entity(name = "stored_block")
public class StoredBlockEntity implements BlockStorageEntry {
  @Id
  private LocationEmbedded location;

  @Column
  private String blockId;

  @Column
  @Lob
  @Basic
  private byte[] content;

  public LocationEmbedded getLocation() {
    return location;
  }

  public void setLocation(LocationEmbedded location) {
    this.location = location;
  }

  public String getBlockId() {
    return blockId;
  }

  public void setBlockId(String blockId) {
    this.blockId = blockId;
  }

  public byte[] getContent() {
    return content;
  }

  public void setContent(byte[] content) {
    this.content = content;
  }


  @Nonnull
  @Override
  public LocationEmbedded location() {
    return this.location;
  }

  @Nonnull
  @Override
  public NamespacedKey blockId() {
    return Objects.requireNonNull(NamespacedKey.fromString(this.blockId));
  }

  @Nonnull
  @Override
  public byte[] content() {
    return content;
  }
}
