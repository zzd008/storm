package cn.jxust.bigdata.storm.kafka_storm_order;


import com.google.gson.Gson;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.UUID;

/**
 * ����javabean
 * �򵥵Ķ�����Ϣ��ʵ��ҵ������ӣ����縸�Ӷ�����һ�����������Ʒ��һ��֧������������������
 * ��Ϊ������Ҫ���Ͷ�����Ϣ��kafka���������紫��Ҫʵ�����л��ӿ�
 */
public class OrderInfo implements Serializable {
    private String orderId;//�������
    private Date createOrderTime;//��������ʱ��
    private String paymentId;//֧�����
    private Date paymentTime;//֧��ʱ��
    private String productId;//��Ʒ���
    private String productName;//��Ʒ����
    private long productPrice;//��Ʒ�۸�
    private long promotionPrice;//�����۸�
    private String shopId;//���̱��
    private String shopName;//��������
    private String shopMobile;//��Ʒ�绰
    private long payPrice;//����֧���۸�
    private int num;//��������

    public OrderInfo() {//�޲ι�����
    	
    }

    public OrderInfo(String orderId, Date createOrderTime, String paymentId, Date paymentTime, String productId, String productName, long productPrice, long promotionPrice, String shopId, String shopName, String shopMobile, long payPrice, int num) {
        this.orderId = orderId;
        this.createOrderTime = createOrderTime;
        this.paymentId = paymentId;
        this.paymentTime = paymentTime;
        this.productId = productId;
        this.productName = productName;
        this.productPrice = productPrice;
        this.promotionPrice = promotionPrice;
        this.shopId = shopId;
        this.shopName = shopName;
        this.shopMobile = shopMobile;
        this.payPrice = payPrice;
        this.num = num;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public Date getCreateOrderTime() {
        return createOrderTime;
    }

    public void setCreateOrderTime(Date createOrderTime) {
        this.createOrderTime = createOrderTime;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public Date getPaymentTime() {
        return paymentTime;
    }

    public void setPaymentTime(Date paymentTime) {
        this.paymentTime = paymentTime;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public long getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(long productPrice) {
        this.productPrice = productPrice;
    }

    public long getPromotionPrice() {
        return promotionPrice;
    }

    public void setPromotionPrice(long promotionPrice) {
        this.promotionPrice = promotionPrice;
    }

    public String getShopId() {
        return shopId;
    }

    public void setShopId(String shopId) {
        this.shopId = shopId;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getShopMobile() {
        return shopMobile;
    }

    public void setShopMobile(String shopMobile) {
        this.shopMobile = shopMobile;
    }

    public long getPayPrice() {
        return payPrice;
    }

    public void setPayPrice(long payPrice) {
        this.payPrice = payPrice;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    @Override
    public String toString() {
        return "PaymentInfo{" +
                "orderId='" + orderId + '\'' +
                ", createOrderTime=" + createOrderTime +
                ", paymentId='" + paymentId + '\'' +
                ", paymentTime=" + paymentTime +
                ", productId='" + productId + '\'' +
                ", productName='" + productName + '\'' +
                ", productPrice=" + productPrice +
                ", promotionPrice=" + promotionPrice +
                ", shopId='" + shopId + '\'' +
                ", shopName='" + shopName + '\'' +
                ", shopMobile='" + shopMobile + '\'' +
                ", payPrice=" + payPrice +
                ", num=" + num +
                '}';
    }

    //������ɶ�����Ϣ
    public String random() {
    	//ʵ��ҵ���У��Ὣ��Ʒ��Ϣ���з��� ��1-10w�ǵ��� 11-30w��ʳƷ�ȵȡ���
        this.productId = "12121212";//��Ʒ���
        this.orderId = UUID.randomUUID().toString().replaceAll("-", "");//�������
        this.paymentId = UUID.randomUUID().toString().replaceAll("-", "");//֧�����
        this.productPrice = new Random().nextInt(1000);//��Ʒ��Ǯ
        this.promotionPrice = new Random().nextInt(500);//�Żݼ�
        this.payPrice = new Random().nextInt(480);//����֧���۸�

        String date = "2018-2-11 12:22:12";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            this.createOrderTime = simpleDateFormat.parse(date);//��������ʱ��
        } catch (ParseException e) {
            e.printStackTrace();
        }
        //��javabean����תΪjson��������kafka
        return new Gson().toJson(this);
    }
}
