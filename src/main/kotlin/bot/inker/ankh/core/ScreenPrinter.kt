package bot.inker.ankh.core

import bot.inker.ankh.core.libs.paperlib.PaperLib
import org.slf4j.Logger

/**
 * ScreenPrinter print a welcome-page in console
 * It will print logo and suggest papermc if need
 */
object ScreenPrinter {
  fun print(logger: Logger) {
    logger.info(
      """AnkhCore Logo Print
    ___          __   __    ______              
   /   |  ____  / /__/ /_  / ____/___  ________ 
  / /| | / __ \/ //_/ __ \/ /   / __ \/ ___/ _ \
 / ___ |/ / / / ,< / / / / /___/ /_/ / /  /  __/
/_/  |_/_/ /_/_/|_/_/ /_/\____/\____/_/   \___/ 
         AnkhCore 1.0-SNAPSHOT Loading          
"""
    )
    if (!PaperLib.isPaper()) {
      logger.info("====================================================")
      logger.info(" AnkhCore works better if you use Paper as your server software.")
      logger.info(" AnkhCore may cause lag without Paper, will cause blocking-io in main-thread.")
      logger.info(" If you config data-backends likes database, lag will be more serious.")
      if (System.getProperty("paperlib.shown-benefits") == null) {
        System.setProperty("paperlib.shown-benefits", "1")
        logger.info("  ")
        logger.info(" Paper offers significant performance improvements,")
        logger.info(" bug fixes, security enhancements and optional")
        logger.info(" features for server owners to enhance their server.")
        logger.info("  ")
        logger.info(" Paper includes Timings v2, which is significantly")
        logger.info(" better at diagnosing lag problems over v1.")
        logger.info("  ")
        logger.info(" All of your plugins should still work, and the")
        logger.info(" Paper community will gladly help you fix any issues.")
        logger.info("  ")
        logger.info(" Join the Paper Community @ https://papermc.io")
      }
      logger.info("====================================================")
    }
  }
}