package base

import models.Command

abstract class BaseUseCase {

    abstract val commands: List<Command>

    abstract fun start(command: Command, args: Array<String>): UseCaseResult

}