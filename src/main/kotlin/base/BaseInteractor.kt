package base

abstract class BaseInteractor {

    protected fun requireString(name : String) : String {
        print("Enter $name: ")
        return readLine()!!.trim()
    }

}