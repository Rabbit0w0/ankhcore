package org.inksnow.ankh.core.world.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.kyori.adventure.key.Key;
import org.inksnow.ankh.core.api.world.storage.BlockStorageEntry;
import org.inksnow.ankh.core.common.entity.LocationEmbedded;

import javax.annotation.Nonnull;

@Entity(name = "stored_block")
@Access(AccessType.FIELD)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StoredBlockEntity implements BlockStorageEntry {
  @Id
  private LocationEmbedded location;

  @Column
  private String blockId;

  @Column
  @Lob
  @Basic
  private byte[] content;

  public StoredBlockEntity(LocationEmbedded warp, Key blockId, byte[] content) {
    this(warp, blockId.asString(), content);
  }

  @Override
  public @Nonnull Key blockId() {
    return Key.key(this.blockId);
  }

  public StoredBlockEntity blockId(Key blockId) {
    this.blockId = blockId.asString();
    return this;
  }
}
