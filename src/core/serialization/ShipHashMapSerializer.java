package core.serialization;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.deser.AbstractDeserializer;
import core.communication_data.ShipID;

import java.io.IOException;
import java.io.StringWriter;

public class ShipHashMapSerializer extends JsonSerializer<ShipID> {

    private ObjectMapper mapper = new ObjectMapper();

    @Override
    public void serialize(ShipID shipID, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        StringWriter writer = new StringWriter();
        mapper.writeValue(writer, shipID.getId());
        jsonGenerator.writeFieldName(writer.toString());
    }
}
