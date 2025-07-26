import com.ride.driver.backend.exceptions.httpException

open class customException(
    message: String? = "Custom exception occurred"
) : httpException("400", message)
