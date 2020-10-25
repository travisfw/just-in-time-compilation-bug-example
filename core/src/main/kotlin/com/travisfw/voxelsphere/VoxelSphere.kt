package com.travisfw.voxelsphere

import com.badlogic.gdx.ApplicationListener
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class VoxelSphere : ApplicationListener {

  var res: GraphicsResources? = null
  override fun create() {
    Modl.initialize()
    res = GraphicsResources()
  }


  override fun resize(width: Int, height: Int) {
    val res = res ?: return
    with (res.camera) {
      viewportHeight = cmPerPx(height)
      viewportWidth = cmPerPx(width)
      update()
    }
  }

  override fun render() {
    val res = res
    if (res == null) {
      log.error("null resources; can't render!")
      return
    }
    with (Gdx.gl) {
      glViewport(0, 0, Gdx.graphics.width, Gdx.graphics.height)
      glClear(GL20.GL_COLOR_BUFFER_BIT or GL20.GL_DEPTH_BUFFER_BIT)
    }
    res.batch.begin(res.camera)
    res.batch.render(res.modelInstances, res.environment)
    res.batch.end()
    // rotate
    val axis = Vector3.X
    val speed = 20
    res.cubeInstance.transform.rotate(axis, Gdx.graphics.deltaTime *speed)
    res.cubeGrid.transform.rotate(axis, Gdx.graphics.deltaTime *speed)
  }

  override fun pause() {}
  override fun resume() {}

  override fun dispose() {
    Modl.dispose()
    val res = res ?: return
    this.res = null
    res.dispose()
  }

  companion object {
    val log: Logger = LoggerFactory.getLogger("voxelsphere.core")
    fun cmPerPx(px: Number) = px.toFloat()/ pixelsPerCentimeter
    var pixelsPerCentimeter: Float = 5F
    @JvmStatic
    public var defaultTargetResolution = Vector2(1870F, 1080F)

  }
}
