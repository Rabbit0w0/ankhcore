package org.inksnow.ankh.testplugin.item

import net.kyori.adventure.key.Key
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Material
import org.bukkit.event.block.Action
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.inksnow.ankh.core.api.plugin.AnkhPluginContainer
import org.inksnow.ankh.core.api.plugin.annotations.AutoRegistered
import org.inksnow.ankh.core.api.world.WorldService
import org.inksnow.ankh.core.item.AbstractAnkhItem
import org.inksnow.ankh.testplugin.block.TestBlock
import org.slf4j.LoggerFactory
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@AutoRegistered
class TestItem @Inject private constructor(
  private val pluginContainer: AnkhPluginContainer,
  private val worldService: WorldService,
  private val testBlockFactory: TestBlock.Factory,
) : AbstractAnkhItem() {
  private val logger = LoggerFactory.getLogger(this.javaClass)

  override fun key(): Key = Key.key(pluginContainer.plugin().name, "test-item")
  override fun material(): Material = Material.STONE

  override fun itemName(): Component = Component.text()
    .append(Component.text("测", NamedTextColor.BLUE))
    .append(Component.text("试", NamedTextColor.RED))
    .append(Component.text("物", NamedTextColor.GREEN))
    .append(Component.text("品", NamedTextColor.YELLOW))
    .build()

  override fun lores(): List<Component> = listOf(
    Component.text()
      .append(Component.text("测试物品，点击放置 测试方块"))
      .build()
  )

  override fun onBlockPlace(event: BlockPlaceEvent) {
    worldService.setBlock(event.blockPlaced.location, testBlockFactory.create())
  }

  override fun onUseItem(event: PlayerInteractEvent) {
    event.player.sendMessage("You use test block as action ${event.action.name}")
    when (event.action) {
      Action.RIGHT_CLICK_BLOCK -> {
        return // TODO: use item
        val clickedBlock = event.clickedBlock ?: return
        worldService.setBlock(
          clickedBlock.location.add(event.blockFace.direction),
          testBlockFactory.create()
        )
      }

      else -> {}
    }
  }
}