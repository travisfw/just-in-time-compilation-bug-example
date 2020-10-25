package com.travisfw.voxelsphere.lwjgl3

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration
import com.travisfw.voxelsphere.VoxelSphere
import com.travisfw.voxelsphere.VoxelSphere.Companion.defaultTargetResolution

/** Launches the desktop (LWJGL3) application.  */
object Lwjgl3Launcher {
  @JvmStatic
  fun main(args: Array<String>) {
    createApplication()
  }

  private fun createApplication() =
      Lwjgl3Application(VoxelSphere(), defaultConfiguration)

  private val defaultConfiguration: Lwjgl3ApplicationConfiguration
    get() {
      val configuration = Lwjgl3ApplicationConfiguration()
      configuration.setTitle("VoxelSphere")
      configuration.setWindowedMode(
          defaultTargetResolution.x.times(windowScale).toInt(),
          defaultTargetResolution.y.times(windowScale).toInt())
      configuration.setWindowIcon("libgdx128.png", "libgdx64.png", "libgdx32.png", "libgdx16.png")
      return configuration
    }
  const val windowScale = 1.6f
}
