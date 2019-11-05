package com.intel.hpnl.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class HpnlConfig {

  private String providerName;
  private String fabricFilename;
  private HpnlService.EndpointType endpointType;
  private String nic;
  private String appId;
  private int portBatchSize;

  private int bufferNumTiny;
  private int bufferNumSmall;
  private int bufferNumMedium;
  private int bufferNumLarge;
  private int bufferNumMax;

  private boolean autoAckConnection;

  private int ctxNum;
  private int srvCtxNum;

  private int readBatchSize;

  public static final String DEFAULT_ENDPOINT_TYPE = "RDM";
  public static final String DEFAULT_APP_ID = "default";
  public static final String DEFAULT_PORT_BATCH_SIZE = "50";
  public static final String DEFAULT_PROVIDER_NAME = "sockets";
  public static final String DEFAULT_LIBFABRIC_FILE_NAME = "libfabric.so";

  public static final String DEFAULT_CONTEXT_NUM = "1024";
  public static final String DEFAULT_CONTEXT_NUM_SERVER = "2048";

  public static final String DEFAULT_READ_BATCH_SIZE = "10";

  public static final String DEFAULT_BUFFER_NUM_SMALL = "10240"; //10m, for most RPC other than launch task
  public static final String DEFAULT_BUFFER_NUM_LARGE = "2560"; //20m, for RPC launch task
  public static final String DEFAULT_BUFFER_NUM_MAX = "256"; //buffer with max size which is equal to send buffer size

  public static final String DEFAULT_AUTO_ACK_CONNECTION = "false";

  private static HpnlConfig _instance = new HpnlConfig();

  private static final Logger log = LoggerFactory.getLogger(HpnlConfig.class);

  private HpnlConfig(){
    String path = "/hpnl/hpnl.conf";

    try(InputStream is = HpnlConfig.class.getResourceAsStream(path)){
      Properties properties = new Properties();
      properties.load(is);
      providerName = getValue(properties, Constants.CONFIG_ITEM_PROVIDER_NAME, DEFAULT_PROVIDER_NAME);

      fabricFilename = getValue(properties, Constants.CONFIG_ITEM_LIBFABRIC_FILE_NAME, DEFAULT_LIBFABRIC_FILE_NAME);

      String endPointValue = getValue(properties, Constants.CONFIG_ITEM_END_POINT_TYPE, DEFAULT_ENDPOINT_TYPE);
      endpointType = HpnlService.EndpointType.valueOf(endPointValue);

      nic = getValue(properties, Constants.CONFIG_ITEM_NIC_NAME, null);

      appId = getValue(properties, Constants.CONFIG_ITEM_APP_ID, DEFAULT_APP_ID);

      String tmp = getValue(properties, Constants.CONFIG_ITEM_PORT_BATCH_SIZE, DEFAULT_PORT_BATCH_SIZE);
      portBatchSize = Integer.valueOf(tmp);

      tmp = getValue(properties, Constants.CFG_BUFFER_NUM_SMALL, DEFAULT_BUFFER_NUM_SMALL);
      bufferNumSmall = Integer.valueOf(tmp);
      tmp = getValue(properties, Constants.CFG_BUFFER_NUM_LARGE, DEFAULT_BUFFER_NUM_LARGE);
      bufferNumLarge = Integer.valueOf(tmp);
      tmp = getValue(properties, Constants.CFG_BUFFER_NUM_MAX, DEFAULT_BUFFER_NUM_MAX);
      bufferNumMax = Integer.valueOf(tmp);

      tmp = getValue(properties, Constants.CFG_AUTO_ACK_CONNECTION, DEFAULT_AUTO_ACK_CONNECTION);
      autoAckConnection = Boolean.valueOf(tmp);

      tmp = getValue(properties, Constants.CFG_CONTEXT_NUM, DEFAULT_CONTEXT_NUM);
      ctxNum = Integer.valueOf(tmp);
      tmp = getValue(properties, Constants.CFG_CONTEXT_NUM_SERVER, DEFAULT_CONTEXT_NUM_SERVER);
      srvCtxNum = Integer.valueOf(tmp);

      tmp = getValue(properties, Constants.CFG_READ_BATCH_SIZE, DEFAULT_READ_BATCH_SIZE);
      readBatchSize = Integer.valueOf(tmp);
    } catch (IOException e) {
      log.error("failed to read hpnl config from "+path, e);
    }
  }

  private String getValue(Properties properties, String key, String defaultValue){
    String value = System.getProperty(key, properties.getProperty(key));
    if (!hasValue(value)) {
      value = defaultValue;
    }
    return value;
  }

  private static boolean hasValue(String value){
    return value != null && value.trim().length()>0;
  }

  public static HpnlConfig getInstance(){
    return _instance;
  }

  public HpnlService.EndpointType getEndpointType() {
    return endpointType;
  }

  public String getLibfabricProviderName() {
    return providerName;
  }

  public String getLibfabricFileName() {
    return fabricFilename;
  }

  public String getNic(){
    return nic;
  }

  public String getAppId() {
    return appId;
  }

  public int getPortBatchSize() {
    return portBatchSize;
  }

  public int getCtxNum() {
    return ctxNum;
  }

  public int getSrvCtxNum() {
    return srvCtxNum;
  }

  public int getBufferNumSmall() {
    return bufferNumSmall;
  }

  public int getBufferNumLarge() {
    return bufferNumLarge;
  }

  public int getBufferNumMax() {
    return bufferNumMax;
  }

  public boolean isAutoAckConnection (){
    return autoAckConnection;
  }

  public int getReadBatchSize() {
    return readBatchSize;
  }


}
