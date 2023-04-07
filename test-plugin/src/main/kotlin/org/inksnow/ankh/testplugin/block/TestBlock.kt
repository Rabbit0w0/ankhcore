package org.inksnow.ankh.testplugin.block

import com.google.inject.Injector
import net.kyori.adventure.key.Key
import org.bukkit.Location
import org.bukkit.Material
import org.inksnow.ankh.core.api.block.AnkhBlock
import org.inksnow.ankh.core.api.block.AsyncTickableBlock
import org.inksnow.ankh.core.api.block.TickableBlock
import org.inksnow.ankh.core.api.hologram.HologramService
import org.inksnow.ankh.core.api.hologram.HologramTask
import org.inksnow.ankh.core.api.plugin.AnkhPluginContainer
import org.inksnow.ankh.core.api.plugin.annotations.AutoRegistered
import org.inksnow.ankh.testplugin.item.TestItem
import org.slf4j.LoggerFactory
import java.util.*
import java.util.concurrent.atomic.AtomicReference
import javax.inject.Inject
import javax.inject.Singleton

class TestBlock private constructor(
  private val factory: Factory,
) : AnkhBlock, TickableBlock, AsyncTickableBlock {
  private var location: Location? = null
  private var hologram: HologramTask? = null
  private var nextMaterial = AtomicReference(Material.DIAMOND_BLOCK)

  override fun key(): Key = factory.blockId

  override fun runTick() {
    val location = this.location ?: return

    nextMaterial.set(factory.materials[factory.random.nextInt(factory.materials.size)])
    location.block.type = nextMaterial.get()

    this.hologram?.let { hologram ->
      hologram.updateContent(
        factory.hologramService.content()
          .appendContent(nextMaterial.get().name)
          .build()
      )
    }
  }

  override fun runAsyncTick() {
    // nextMaterial.set(materials[random.nextInt(materials.size)])
  }

  override fun load(location: Location) {
    this.location = location
    location.block.type = nextMaterial.get()
    this.hologram = factory.hologramService.builder()
      .location(location.clone().add(0.5, 1.5, 0.5))
      .build()
  }

  override fun unload() {
    this.hologram?.delete()
  }

  override fun remove(isDestroy: Boolean) {
    val location = this.location

    hologram?.delete()

    if (isDestroy && location != null) {
      location.world.dropItemNaturally(location, factory.testItem.createItem())
    }
  }

  @Singleton
  @AutoRegistered
  class Factory @Inject private constructor(
    val injector: Injector,
    val hologramService: HologramService,
    val pluginContainer: AnkhPluginContainer,
  ) : AnkhBlock.Factory {
    val testItem by lazy { injector.getInstance(TestItem::class.java) }
    val logger = LoggerFactory.getLogger(this.javaClass)
    val random = Random()
    val materials = arrayOf(
      Material.IRON_BLOCK,
      Material.GOLD_BLOCK,
      Material.DIAMOND_BLOCK
    )
    val blockId = Key.key(pluginContainer.plugin().name, "test-block")

    override fun key(): Key = blockId

    override fun load(id: Key, data: ByteArray): AnkhBlock {
      require(id == blockId)
      return TestBlock(this)
    }

    fun create(): AnkhBlock {
      return TestBlock(this)
    }
  }
}