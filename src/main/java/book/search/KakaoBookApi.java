package book.search;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

// Kakao API 사용
public class KakaoBookApi {
    private static final String API_KEY = "930e83548c0212a56b60599f78815079";    // Rest API Key
    private static final String API_BASE_URL = "https://dapi.kakao.com/v3/search/book";
    private static final OkHttpClient client = new OkHttpClient();
    private static final Gson gson = new Gson();

    // 책 검색 메서드
    public static List<Book> searchBooks(String title) throws IOException {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(API_BASE_URL).newBuilder();  // URL을 연결하는 객체 생성
        urlBuilder.addQueryParameter("query", title);   //  API에 질의할 항목과 타겟을 전달, 질의할 때 KakaoAPI에서 필수로 필요한 구문 "query"

        Request request = new Request.Builder() // 서버에 요청하는 객체
                .url(urlBuilder.build())
                .addHeader("Authorization", "KakaoAK " + API_KEY)   // Kakao API 인증 정보 전달
                .build();   // 요청을 빌드 후 서버에 요청

        try(Response response = client.newCall(request).execute()) {    // OkHttpClient 라이브러리, newCall 메서드로 요청을 실행 후 응답을 받는 객체
            if(!response.isSuccessful()) throw new IOException("Request failed: " + response);

            JsonObject jsonResponse = gson.fromJson(response.body().charStream(), JsonObject.class);    // Responce.body는 요청의 응답으로 JSON 형식의 데이터를 의미, 이것을 charStream으로 변경한 후 JSON로 변환
            JsonArray documents = jsonResponse.getAsJsonArray("documents"); // KaKao API로 받은 책의 정보는 document 객체에 포함되어 있다.

            List<Book> books = new ArrayList<>();
            for(JsonElement document : documents) {
                JsonObject bookJson = document.getAsJsonObject();
                Book book = new Book(
                        bookJson.get("title").getAsString(),
                        bookJson.get("authors").getAsJsonArray().toString(),
                        bookJson.get("publisher").getAsString(),
                        bookJson.get("thumbnail").getAsString()
                );
                books.add(book);
            }
            return books;
        }
    }
}
