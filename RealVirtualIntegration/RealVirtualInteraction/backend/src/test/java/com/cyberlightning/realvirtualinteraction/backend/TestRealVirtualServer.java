package com.cyberlightning.realvirtualinteraction.backend;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;

import com.cyberlightning.realvirtualinteraction.backend.entities.Entity;
import com.cyberlightning.realvirtualinteraction.backend.services.DataStorageService;
import com.cyberlightning.realvirtualinteraction.backend.services.Gzip;
import com.cyberlightning.realvirtualinteraction.backend.services.TranslationService;
import com.cyberlightning.realvirtualinteraction.backend.sockets.MessageObject;
import com.cyberlightning.realvirtualinteraction.backend.sockets.UdpSocket;

public class TestRealVirtualServer {

	public final String testJsonString ="{\"d23c0586984d35eff\":{\"d23c058698435eff\":{\"sensors\":[{\"value\":{\"unit\":\"orientation\",\"primitive\":\"3DPoint\",\"time\":\"2013-11-15 14:56\",\"values\":[-0.003545045852661133,0.05859129875898361,-0.5206212997436523]},\"parameters\":{\"interval\":\"ms\",\"toggleable\":\"boolean\"},\"attributes\":{\"type\":\"orientation\",\"power\":1.5,\"vendor\":\"Samsung Inc.\",\"name\":\"Orientation Sensor\"}},{\"value\":{\"unit\":\"rad/s\",\"primitive\":\"3DPoint\",\"time\":\"2013-11-15 14:56\",\"values\":[9.9683837890625,0.23239292204380035,-1.8811875581741333]},\"parameters\":{\"interval\":\"ms\",\"toggleable\":\"boolean\"},\"attributes\":{\"type\":\"gyroscope\",\"power\":0.5,\"vendor\":\"Invensense\",\"name\":\"MPL Gyro\"}},{\"value\":{\"unit\":\"lx\",\"primitive\":\"double\",\"time\":\"2013-11-15 14:56\",\"values\":357.77637},\"parameters\":{\"interval\":\"ms\",\"toggleable\":\"boolean\"},\"attributes\":{\"type\":\"light\",\"power\":0.75,\"vendor\":\"Capella\",\"name\":\"CM3663 Light sensor\"}},{\"value\":{\"unit\":\"uT\",\"primitive\":\"3DPoint\",\"time\":\"2013-11-15 14:56\",\"values\":[357.81671142578125,0.5156025290489197,-1.8891750574111938]},\"parameters\":{\"interval\":\"ms\",\"toggleable\":\"boolean\"},\"attributes\":{\"type\":\"magneticfield\",\"power\":0.5,\"vendor\":\"Invensense\",\"name\":\"MPL magnetic field\"}},{\"value\":{\"unit\":\"m/s2\",\"primitive\":\"3DPoint\",\"time\":\"2013-11-15 14:56\",\"values\":[-0.3239738643169403,-0.09122344106435776,9.800872802734375]},\"parameters\":{\"interval\":\"ms\",\"toggleable\":\"boolean\"},\"attributes\":{\"type\":\"linearacceleration\",\"power\":0.5,\"vendor\":\"Invensense\",\"name\":\"MPL linear accel\"}},{\"value\":{\"unit\":\"m/s2\",\"primitive\":\"3DPoint\",\"time\":\"2013-11-15 14:56\",\"values\":[-0.32213011384010315,-0.0398171991109848,9.804611206054688]},\"parameters\":{\"interval\":\"ms\",\"toggleable\":\"boolean\"},\"attributes\":{\"type\":\"accelerometer\",\"power\":0.5,\"vendor\":\"Invensense\",\"name\":\"MPL accel\"}},{\"value\":{\"unit\":\"quaternion\",\"primitive\":\"array\",\"time\":\"2013-11-15 14:56\",\"values\":[357.8206787109375,0.5172339677810669,-1.8906971216201782]},\"parameters\":{\"interval\":\"ms\",\"toggleable\":\"boolean\"},\"attributes\":{\"type\":\"rotationvector\",\"power\":1.5,\"vendor\":\"Google Inc.\",\"name\":\"Rotation Vector Sensor\"}},{\"value\":{\"unit\":\"m/s2\",\"primitive\":\"3DPoint\",\"time\":\"2013-11-15 14:56\",\"values\":[-0.0030828863382339478,-8.415747433900833E-4,0.003661018330603838]},\"parameters\":{\"interval\":\"ms\",\"toggleable\":\"boolean\"},\"attributes\":{\"type\":\"gravity\",\"power\":0.5,\"vendor\":\"Invensense\",\"name\":\"MPL gravity\"}}],\"attributes\":{\"gps\":[65.5,25.3],\"name\":\"Android device\"}}}}";
	public final String testUdpSocketUUID = UUID.randomUUID().toString();
	public final String testHttpPostQuery = "action=update&deviceid=d23c058698435eff";
	public final byte[] testClientAddress = {127,0,0,1};
	public final int type = StaticResources.UDP_RECEIVER;
	public final int testClientPort = 61616;
	
	public DatagramPacket testCompressedPacket;
	public DatagramPacket testUnCompressedPacket;
	public MessageObject testClientMessage;
	public UdpSocket udpSocket;
	
	
	@Before
	public void prepareCompressedMessageObject() {
		try {
			byte[] byteBuffer = Gzip.compress(this.testJsonString);
			this.testCompressedPacket = new DatagramPacket(byteBuffer, byteBuffer.length,InetAddress.getByAddress(this.testClientAddress), this.testClientPort);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Before
	public void prepareUnCompressedMessageObject() {
		try {
			byte[] byteBuffer = this.testJsonString.getBytes();
			this.testUnCompressedPacket = new DatagramPacket(byteBuffer, byteBuffer.length,InetAddress.getByAddress(this.testClientAddress), this.testClientPort);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Before
	public void prepareEmptyCompressedMessageObject() {
		try {
			byte[] byteBuffer = null;
			try {
				byteBuffer = Gzip.compress("");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			this.testCompressedPacket = new DatagramPacket(byteBuffer, byteBuffer.length,InetAddress.getByAddress(this.testClientAddress), this.testClientPort);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	@Test 
	public void testCreatingMessageObjectFromCompressedPacket(){
		assertNotNull("MessageObject should not be null", new MessageObject(this.testUdpSocketUUID,this.type,this.testCompressedPacket));
	}
	
	@Test 
	public void testCreatingMessageObjectFromUnCompressedPacket(){
		assertNotNull("MessageObject should not be null", new MessageObject(this.testUdpSocketUUID,this.type,this.testUnCompressedPacket));
	}
	
	@Test
	public void testDecodeJsonStringToReturnCorrectNumberOfEntities() {
		assertEquals("Decoding failed: Result does not equal 1",1, TranslationService.decodeSensorJson(this.testJsonString).size());
	}
	
	@Test
	public void testDecodeJsonStringToReturnCorrectlyFormedEntity() {
		assertEquals("Decoding failed: ContextUUID not as expected" + TranslationService.decodeSensorJson(this.testJsonString).get(0).contextUUID,"d23c0586984d35eff", TranslationService.decodeSensorJson(this.testJsonString).get(0).contextUUID);
	}
	
	@Test
	public void testEncodeJsonStringToReturnCorrectlyFormedEntity() {
		ArrayList<Entity> entities = TranslationService.decodeSensorJson(this.testJsonString);
		assertEquals("Encoding failed: JSON strings not matching", this.testJsonString, TranslationService.encodeJson(entities, 1));
	}
	
	@Test
	public void testDataStorageServiceSaveDatagramPacketWithCompressedPayload() {
		
		boolean isSuccess = true;
		this.prepareCompressedMessageObject();
		
		try {
			DataStorageService.getInstance().addEntry(this.testCompressedPacket);
		} catch (IOException e) {
			isSuccess = false;
		}
		assertEquals("DataStorageServiceError not being able to store a testCompressedPacket: ",true, isSuccess );
	}
	
	@Test
	public void testDataStorageServiceSaveDatagramPacketWithUnCompressedPayload() {
		
		boolean isSuccess = true;
		this.prepareUnCompressedMessageObject();
		
		try {
			DataStorageService.getInstance().addEntry(this.testUnCompressedPacket);
		} catch (IOException e) {
			isSuccess = false;
		}
		assertEquals("DataStorageServiceError not being able to store a testCompressedPacket: ",true, isSuccess );
	}

}
