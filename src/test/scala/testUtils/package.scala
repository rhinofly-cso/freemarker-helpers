import nl.rhinofly.railosbt.RailoRunner
import nl.rhinofly.railo.compiler.RailoContext

package object testUtils {
  def withRailo[T](code: RailoContext => T): T =
    RailoRunner.withRailo(9191)(code)
    
}