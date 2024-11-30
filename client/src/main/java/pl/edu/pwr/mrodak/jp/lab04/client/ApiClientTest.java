package pl.edu.pwr.mrodak.jp.lab04.client;



public class ApiClientTest {
    public static void main(String[] args) {

        ApiClient client = new ApiClient();
        System.out.println(client.getProvinces());
        System.out.println(client.getAvgLivingSpace(2019));
        System.out.println(client.getAvgLivingSpace(2010, 2023));
    }
}