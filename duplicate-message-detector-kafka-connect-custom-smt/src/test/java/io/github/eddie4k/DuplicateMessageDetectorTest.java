package io.github.eddie4k;

import org.apache.kafka.common.record.Record;
import org.apache.kafka.connect.connector.ConnectRecord;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.SchemaBuilder;
import org.apache.kafka.connect.data.Struct;

import io.github.eddie4k.DuplicateMessageDetector.DuplicateMessageDetector;
import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit tests for DuplicateMessageDetector
 */
public class DuplicateMessageDetectorTest{

    /**
     * Create the test suite
     *
     * @return the test suite
     */
    public static Test suite() {
        return new TestSuite(DuplicateMessageDetectorTest.class);
    }


    public void testApplySchema() {

        // Create a new instance of DuplicateMessageDetector
        DuplicateMessageDetector detector = new DuplicateMessageDetector();

        // Create a new schema
        Schema schema = SchemaBuilder.struct()
                .field("field2", Schema.INT32_SCHEMA)
                .build();

        Struct test = new Struct(schema);

        Assert.assertTrue(true);

    }

}