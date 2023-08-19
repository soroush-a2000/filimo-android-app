package com.movieboxtv.app.api;

import com.movieboxtv.app.config.Config;
import com.movieboxtv.app.entity.Actor;
import com.movieboxtv.app.entity.ApiResponse;
import com.movieboxtv.app.entity.Category;
import com.movieboxtv.app.entity.Channel;
import com.movieboxtv.app.entity.Comment;
import com.movieboxtv.app.entity.Country;
import com.movieboxtv.app.entity.Data;
import com.movieboxtv.app.entity.Genre;
import com.movieboxtv.app.entity.Language;
import com.movieboxtv.app.entity.Package;
import com.movieboxtv.app.entity.Poster;
import com.movieboxtv.app.entity.Season;

import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface apiRest {


    @GET("version/check/{code}/{user}/" + Config.SECURE_KEY + "/")
    Call<ApiResponse> check(@Path("code") Integer code, @Path("user") Integer user);

    @GET("device/{tkn}/" + Config.SECURE_KEY + "/")
    Call<ApiResponse> addDevice(@Path("tkn") String tkn);

    @GET("first/" + Config.SECURE_KEY + "/")
    Call<Data> homeData();

    @GET("search/{query}/" + Config.SECURE_KEY + "/")
    Call<Data> searchData(@Path("query") String query);

    @GET("role/by/poster/{id}/" + Config.SECURE_KEY + "/")
    Call<List<Actor>> getRolesByPoster(@Path("id") Integer id);

    @GET("actor/all/{page}/{search}/" + Config.SECURE_KEY + "/")
    Call<List<Actor>> getActorsList(@Path("page") Integer page, @Path("search") String search);

    @GET("movie/by/{id}/" + Config.SECURE_KEY + "/")
    Call<Poster> getPosterById(@Path("id") Integer id);

    @GET("channel/by/{id}/" + Config.SECURE_KEY + "/")
    Call<Channel> geChannelById(@Path("id") Integer id);


    @GET("movie/random/{genres}/" + Config.SECURE_KEY + "/")
    Call<List<Poster>> getRandomMoivies(@Path("genres") String genres);

    @GET("channel/random/{categories}/" + Config.SECURE_KEY + "/")
    Call<List<Channel>> getRandomChannel(@Path("categories") String categories);

    @FormUrlEncoded
    @POST("user/login/" + Config.SECURE_KEY + "/")
    Call<ApiResponse> login(@Field("mobile") String mobile, @Field("password") String password, @Field("token_notif") String token);

    @FormUrlEncoded
    @POST("user/register/" + Config.SECURE_KEY + "/")
    Call<ApiResponse> register(@Field("username") String username, @Field("password") String password, @Field("token_notif") String token, @Field("image") String image);

    @FormUrlEncoded
    @POST("user/resetpassword/" + Config.SECURE_KEY + "/")
    Call<ApiResponse> resetPassword(@Field("mobile") String mobile);

    @GET("user/password/{id}/{old}/{new_}/" + Config.SECURE_KEY + "/")
    Call<ApiResponse> changePassword(@Path("id") String id, @Path("old") String old, @Path("new_") String new_);


    @FormUrlEncoded
    @POST("comment/channel/add/" + Config.SECURE_KEY + "/")
    Call<ApiResponse> addChannelComment(@Field("user") String user, @Field("key") String key, @Field("id") Integer id, @Field("comment") String comment);


    @GET("comments/by/channel/{id}/" + Config.SECURE_KEY + "/")
    Call<List<Comment>> getCommentsByChannel(@Path("id") Integer id);


    @FormUrlEncoded
    @POST("comment/poster/add/" + Config.SECURE_KEY + "/")
    Call<ApiResponse> addPosterComment(@Field("user") String user, @Field("key") String key, @Field("id") Integer id, @Field("comment") String comment);


    @GET("comments/by/poster/{id}/" + Config.SECURE_KEY + "/")
    Call<List<Comment>> getCommentsByPoster(@Path("id") Integer id);

    @GET("subtitles/by/movie/{id}/" + Config.SECURE_KEY + "/")
    Call<List<Language>> getSubtitlesByPoster(@Path("id") Integer id);

    @GET("subtitles/by/episode/{id}/" + Config.SECURE_KEY + "/")
    Call<List<Language>> getSubtitlesByEpisode(@Path("id") Integer id);


    @FormUrlEncoded
    @POST("rate/poster/add/" + Config.SECURE_KEY + "/")
    Call<ApiResponse> addPosterRate(@Field("user") String user, @Field("key") String key, @Field("poster") Integer poster, @Field("value") float value);


    @FormUrlEncoded
    @POST("rate/channel/add/" + Config.SECURE_KEY + "/")
    Call<ApiResponse> addChannelRate(@Field("user") String user, @Field("key") String key, @Field("channel") Integer channel, @Field("value") float value);

    @FormUrlEncoded
    @POST("poster/add/share/" + Config.SECURE_KEY + "/")
    Call<Integer> addPosterShare(@Field("id") Integer id);

    @FormUrlEncoded
    @POST("channel/add/share/" + Config.SECURE_KEY + "/")
    Call<Integer> addChannelShare(@Field("id") Integer id);

    @GET("movie/by/actor/{id}/" + Config.SECURE_KEY + "/")
    Call<List<Poster>> getPosterByActor(@Path("id") Integer id);

    @FormUrlEncoded
    @POST("support/add/" + Config.SECURE_KEY + "/")
    Call<ApiResponse> addSupport(@Field("email") String email, @Field("name") String name, @Field("message") String message);


    @GET("movie/by/filtres/{genre}/{order}/{page}/" + Config.SECURE_KEY + "/")
    Call<List<Poster>> getMoviesByFiltres(@Path("genre") Integer genre, @Path("order") String order, @Path("page") Integer page);


    @GET("poster/by/filtres/{genre}/{country}/{order}/{page}/" + Config.SECURE_KEY + "/")
    Call<List<Poster>> getPostersByFiltres(@Path("genre") Integer genre, @Path("country") Integer country, @Path("order") String order, @Path("page") Integer page);


    @GET("genre/all/" + Config.SECURE_KEY + "/")
    Call<List<Genre>> getGenreList();

    @GET("serie/by/filtres/{genre}/{order}/{page}/" + Config.SECURE_KEY + "/")
    Call<List<Poster>> getSeriesByFiltres(@Path("genre") Integer genre, @Path("order") String order, @Path("page") Integer page);

    @GET("season/by/serie/{id}/" + Config.SECURE_KEY + "/")
    Call<List<Season>> getSeasonsBySerie(@Path("id") Integer id);

    @Multipart
    @POST("user/edit/" + Config.SECURE_KEY + "/")
    Call<ApiResponse> editProfile(@Part MultipartBody.Part file, @Part("id") Integer id, @Part("key") String key, @Part("name") String name);

    @GET("country/all/" + Config.SECURE_KEY + "/")
    Call<List<Country>> getCountiesList();

    @GET("category/all/" + Config.SECURE_KEY + "/")
    Call<List<Category>> getCategoriesList();

    @GET("channel/by/filtres/{category}/{country}/{page}/" + Config.SECURE_KEY + "/")
    Call<List<Channel>> getChannelsByFiltres(@Path("category") Integer category, @Path("country") Integer country, @Path("page") Integer page);

    @FormUrlEncoded
    @POST("movie/add/download/" + Config.SECURE_KEY + "/")
    Call<Integer> addMovieDownload(@Field("id") Integer id);

    @FormUrlEncoded
    @POST("episode/add/download/" + Config.SECURE_KEY + "/")
    Call<Integer> addEpisodeDownload(@Field("id") Integer id);

    @FormUrlEncoded
    @POST("movie/add/view/" + Config.SECURE_KEY + "/")
    Call<Integer> addMovieView(@Field("id") Integer id);

    @FormUrlEncoded
    @POST("episode/add/view/" + Config.SECURE_KEY + "/")
    Call<Integer> addEpisodeView(@Field("id") Integer id);

    @FormUrlEncoded
    @POST("channel/add/view/" + Config.SECURE_KEY + "/")
    Call<Integer> addChannelView(@Field("id") Integer id);

    @FormUrlEncoded
    @POST("user/buysubscribe/" + Config.SECURE_KEY + "/")
    Call<ApiResponse> buySubscribe(@Field("user") String user, @Field("key") String key, @Field("days") String days, @Field("extension") String extension, @Field("pay_token") String pay_token);

    @FormUrlEncoded
    @POST("user/paygateway/" + Config.SECURE_KEY + "/")
    Call<ApiResponse> buyByPayGateway(@Field("user") String user, @Field("key") String key, @Field("amount") String amount);

    @FormUrlEncoded
    @POST("user/sendcode/" + Config.SECURE_KEY + "/")
    Call<ApiResponse> sendCode(@Field("phone_number") String phone_number);

    @FormUrlEncoded
    @POST("user/checkcode/" + Config.SECURE_KEY + "/")
    Call<ApiResponse> verifyCheckCode(@Field("check_code") String check_code);

    @GET("package/all/" + Config.SECURE_KEY + "/")
    Call<List<Package>> getPackageList();
}

