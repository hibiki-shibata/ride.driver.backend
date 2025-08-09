// import com.fasterxml.jackson.core.JsonGenerator
// import com.fasterxml.jackson.core.JsonParser
// import com.fasterxml.jackson.core.JsonProcessingException
// import com.fasterxml.jackson.databind.DeserializationContext
// import com.fasterxml.jackson.databind.JsonDeserializer
// import com.fasterxml.jackson.databind.JsonNode
// import com.fasterxml.jackson.databind.JsonSerializer
// import com.fasterxml.jackson.databind.SerializerProvider
// import org.springframework.boot.jackson.JsonComponent
// import java.io.IOException

// import com.ride.driver.backend.models.DriverDetails

// @JsonComponent
// class DriverDetailsJsonComponent {

// 	class Serializer : JsonSerializer<DriverDetails>() {
// 		@Throws(IOException::class)
// 		override fun serialize(value: DriverDetails, jgen: JsonGenerator, serializers: SerializerProvider) {
// 			jgen.writeStartObject()
// 			jgen.writeStringField("id", value.id.toString())
//             jgen.writeStringField("name", value.name) 
//             jgen.writeStringField("phoneNumber", value.phoneNumber)
//             jgen.writeStringField("vehicleType", value.vehicleType.toString())
//             jgen.writeObjectField("location", value.location)
//             jgen.writeStringField("assignId", value.assignId)
//             jgen.writeNumberField("rate", value.rate)
//             jgen.writeStringField("status", value.status.toString())
//             jgen.writeObjectField("area", value.area)
//             jgen.writeStringField("driverComments", value.driverComments)            
// 			jgen.writeEndObject()
// 		}
// 	}

	// class Deserializer : JsonDeserializer<DriverDetails>() {
	// 	@Throws(IOException::class, JsonProcessingException::class)
	// 	override fun deserialize(jsonParser: JsonParser, ctxt: DeserializationContext): MyObject {
	// 		val codec = jsonParser.codec
	// 		val tree = codec.readTree<JsonNode>(jsonParser)
	// 		val name = tree["name"].textValue()
	// 		val age = tree["age"].intValue()
	// 		return MyObject(name, age)
	// 	}
	// }

// }