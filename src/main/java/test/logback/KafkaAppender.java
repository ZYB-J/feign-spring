package test.logback;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.ConsoleAppender;
import ch.qos.logback.core.Layout;
import ch.qos.logback.core.encoder.LayoutWrappingEncoder;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.IntegerSerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.util.StringUtils;
import test.util.JsonHelper;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhouyongbo on 2019/12/4.
 */

public class KafkaAppender extends ConsoleAppender<ILoggingEvent> {

    private KafkaTemplate<String,String> kafkaTemplate;
    private String topic = "chloe_socket_log";
    private final String HOSTNAME = System.getenv("HOSTNAME");
    private String bootstrapServers;
    private Layout<ILoggingEvent> layout;

    @Override
    protected void append(ILoggingEvent eventObject) {
        String loggerName = eventObject.getLoggerName();
        if(loggerName.indexOf("kafka") > -1){
            try{
//                LayoutWrappingEncoder encoder = (LayoutWrappingEncoder) this.encoder;
                String msg = this.layout.doLayout(eventObject);
                Map<String,String> result = new HashMap<>();
                result.put("HOSTNAME",HOSTNAME);
                result.put("msg",msg);
                kafkaTemplate.send(topic, JsonHelper.GetJsonResult(result));
            }catch (Exception e){
                e.printStackTrace();
            }
        }else{
            super.append(eventObject);
        }



    }

    @Override
    public void start() {
        if(StringUtils.isEmpty(bootstrapServers)){
            throw new IllegalArgumentException("bootstrapServers not exist");
        }
        this.layout =   ((LayoutWrappingEncoder) this.encoder).getLayout();
        this.kafkaTemplate = new KafkaTemplate<>(producerFactory());
        super.start();
    }

    private ProducerFactory<String, String> producerFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfigs());
    }


    public Map<String, Object> producerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.RETRIES_CONFIG, 0);
        props.put(ProducerConfig.BATCH_SIZE_CONFIG, 16384);
        props.put(ProducerConfig.BUFFER_MEMORY_CONFIG, 3355443);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, IntegerSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.ACKS_CONFIG,"0");
        return props;
    }


    private KafkaTemplate<String, String> kafkaTemplate() {
        KafkaTemplate<String, String> kafkaTemplate = new KafkaTemplate<>(producerFactory());
        return kafkaTemplate;
    }

    @Override
    public void stop() {
        super.stop();
    }

    public String getBootstrapServers() {
        return bootstrapServers;
    }

    public void setBootstrapServers(String bootstrapServers) {
        this.bootstrapServers = bootstrapServers;
    }
}
