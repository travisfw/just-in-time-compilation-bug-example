package com.travisfw.voxelsphere

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.VertexAttributes
import com.badlogic.gdx.graphics.g3d.Material
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder
import com.badlogic.gdx.graphics.g3d.utils.shapebuilders.BoxShapeBuilder
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.math.collision.BoundingBox
import com.travisfw.voxelsphere.VoxelSphere.Companion.log
import ktx.math.minus
import ktx.math.plus
import kotlin.math.abs
import kotlin.math.sqrt

/**
 * Access hard-coded [Model]s.
 */
enum class Modl {
  Cube,
  Grid,
  CubeGrid,
  ;

  fun get() = mdls[this] ?: throw IllegalStateException(
      "model $this not initialized")

  companion object {
    private val mdls: MutableMap<Modl, Model> = mutableMapOf()

    fun initialize() {
      val voxSpecular = ColorAttribute.createSpecular(0.2f, 0.2f, 0.2f, 1f)

      val blueM = Material(ColorAttribute.createDiffuse(Color.BLUE))
      val blankM = Material(voxSpecular, ColorAttribute.createDiffuse(Color.SLATE))
      val redM = Material(ColorAttribute.createDiffuse(Color.RED))
      val bitAtts = (
          VertexAttributes.Usage.Position or
          VertexAttributes.Usage.Normal).toLong()

      val modelBuilder = ModelBuilder()

      mdls[Cube] = modelBuilder
          .createXYZCoordinates(1F, blueM, bitAtts)

      mdls[Grid] = modelBuilder
          .createLineGrid(64, 64, 0.2f, 0.2f, redM, bitAtts)

      mdls[CubeGrid] = modelBuilder.run { begin()
        val gridSize = 4.0f
        val halfSize: Float = gridSize / 2
        val gridBound = BoundingBox(
            Vector3(-halfSize, -halfSize, -halfSize),
            Vector3( halfSize,  halfSize,  halfSize))
        val gridResolution = 64 // measured in voxels
        val halfGridResln = gridResolution /2f
        val gridCenter = Vector3(halfGridResln, halfGridResln, halfGridResln)
        val gridCellSize = gridSize /gridResolution
        val surfaceDepth: Float = (gridCellSize *2.0 * sqrt(3.0)).toFloat()
        // diagonal from center to a corner (ie through an eighth of the cube, an octrant)
        val octrantDiagonalSquared = halfSize *halfSize
        val voxelSize: Float = gridSize /gridResolution /2
        val voxelBound = BoundingBox(Vector3(-voxelSize,-voxelSize,-voxelSize), Vector3(voxelSize, voxelSize, voxelSize))
        fun radiusSquared(index: Int): Float {
          val bar = index *gridCellSize - halfSize
          return abs(bar * bar )
        }
        var nbrCubes = 0
        // number of vertexes used in the mesh part so far
        var vertexCount = 0
        fun startPart(): MeshPartBuilder {
          val partName = "gridPart$nbrCubes"
//          log.atTrace().addArgument(partName)
//              .log("starting part $partName")
          vertexCount = 0
          return part(partName, GL20.GL_TRIANGLES, bitAtts, blankM)
        }
        var mpb: MeshPartBuilder = startPart()
        for (x in 0..gridResolution)
          for (y in 0..gridResolution)
            for (z in 0..gridResolution) {
              val cornerPosition = vec3(x, y, z)
              val sumOfCubes = radiusSquared(x) + radiusSquared(y) + radiusSquared(z)
              val centeredPos = cornerPosition - gridCenter
              val taxiFromCenter = centeredPos.taxiDistance()
              if (taxiFromCenter > 1 &&
                  (sumOfCubes -surfaceDepth > octrantDiagonalSquared ||
                  sumOfCubes +surfaceDepth < octrantDiagonalSquared ))
                continue
              if (taxiFromCenter > halfGridResln *1.65) {
//                log.atDebug().addKeyValue("x", centeredPos.x).addKeyValue("y", centeredPos.y).addKeyValue("z", centeredPos.z)
//                    .addKeyValue("taxiD", taxiFromCenter).log("")
                continue
              }
              nbrCubes++
              vertexCount += 8
              if(vertexCount > 8192)
                mpb = startPart()
              val boxOffset = Vector3(
                  gridBound.min.x + x *gridCellSize,
                  gridBound.min.y + y *gridCellSize,
                  gridBound.min.z + z *gridCellSize)
              val translatedCubeBox = voxelBound + boxOffset
              BoxShapeBuilder.build(mpb, translatedCubeBox)
            }
        log.atInfo().addArgument(nbrCubes).log(
            "{} cubes in Grid")
        end() }

    }
    operator fun BoundingBox.plus(distance: Vector3) =
        BoundingBox(this.min + distance, this.max + distance)

    fun dispose() {
      for ((_, mo: Model) in mdls)
        mo.dispose()
      mdls.clear()
    }
  }
}

/** AKA [rectilinear distance](https://en.wikipedia.org/wiki/Taxicab_geometry ). */
private fun Vector3.taxiDistance(): Float = abs(x)+abs(y)+abs(z)

/** converts whatever numbers you have into floats to construct a [Vector3]. */
fun vec3(x: Number = 0f, y: Number = 0f, z: Number = 0f) =
    Vector3(x.toFloat(), y.toFloat(), z.toFloat())
