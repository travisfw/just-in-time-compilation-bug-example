package com.travisfw.voxelsphere

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.PerspectiveCamera
import com.badlogic.gdx.graphics.g3d.Environment
import com.badlogic.gdx.graphics.g3d.ModelBatch
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute.AmbientLight
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight

class GraphicsResources(
    var batch: ModelBatch = ModelBatch(),
    var environment: Environment = Environment().apply {
          set(ColorAttribute(AmbientLight, 0.4F, 0.4F, 0.4F, 1F))
          add(DirectionalLight().set(0.8F, 0.8F, 0.8F, -1F, -0.8F, -0.2F))
        },
    var camera: PerspectiveCamera = PerspectiveCamera(
        initialFieldOfViewY, Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())
        .apply {
          position.set(10F, 10F, 10F)
          lookAt(0F, 0F, 0F)
          near = 1F
          far = 300F
          update()
        },
) {

  val cubeInstance = ModelInstance(Modl.Cube.get(), 0f, 3f, 5f)
  val cubeGrid = ModelInstance(Modl.CubeGrid.get(), 5f, 5f, 5f)
  val gridInstance = ModelInstance(Modl.Grid.get())

  var modelInstances = mutableSetOf(
      cubeInstance,
      cubeGrid,
      gridInstance,
  )

  fun dispose() {
    batch.dispose()
  }

  companion object {
    /** @see PerspectiveCamera.fieldOfView */
    const val initialFieldOfViewY: Float = 30F
  }
}
