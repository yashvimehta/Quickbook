package com.example.quickbook.FragmentSetAdmin;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.quickbook.AdminHomePage;
import com.example.quickbook.ApiHelper.ApiInterface;
import com.example.quickbook.ApiHelper.SearchBookResult;
import com.example.quickbook.BookInfo;
import com.example.quickbook.R;
import com.example.quickbook.SearchCardAdapter;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SearchPageFragment extends Fragment {

    EditText bookNameEditText;

    TextView text;

    FrameLayout frameLayout;
    Button searchButton;
    SearchCardAdapter mCustomCardAdapter;
    ListView booksListView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_search_page, container, false);

        bookNameEditText = view.findViewById(R.id.bookNameEditText );
        text = (TextView) view.findViewById(R.id.textSearch);
        frameLayout = (FrameLayout) view.findViewById(R.id.frame);
        searchButton=view.findViewById(R.id.searchButton);
        booksListView=view.findViewById(R.id.booksListView);

        
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String searchName = bookNameEditText.getText().toString();
                if (searchName.equals("")) {
                    Toast.makeText(getContext(), "Search name cannot be empty", Toast.LENGTH_SHORT).show();
                }
                else {
                    try {
                        final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                                .readTimeout(60, TimeUnit.SECONDS)
                                .connectTimeout(60, TimeUnit.SECONDS)
                                .build();
                        Retrofit retrofit = new Retrofit.Builder()
                                .baseUrl(ApiInterface.BASE_URL_PREDICTOR)
                                .addConverterFactory(GsonConverterFactory.create())
                                .client(okHttpClient)
                                .build();

                        ApiInterface apiInterface = retrofit.create(ApiInterface.class);
                        Call<SearchBookResult> mCall = apiInterface.getSearchBookResult(searchName);
                        mCall.enqueue(new Callback<SearchBookResult>() {
                            @Override
                            public void onResponse(Call<SearchBookResult> call, Response<SearchBookResult> response) {
                                SearchBookResult mResult = response.body();
                                if (mResult.getSuccess()) {
                                    int length=mResult.getAnswer().size();
                                    if(length==0){
                                        Toast.makeText(getContext(), "No such book exists!", Toast.LENGTH_SHORT).show();
                                        booksListView.setVisibility(View.INVISIBLE);
                                    }
                                    else {
                                        ArrayList<String[]> cardArrayList = new ArrayList<String[]>();
                                        cardArrayList.clear();
                                        for (int i = 0; i < length; i++) {
                                            HashMap<String, String> record = (HashMap<String, String>) mResult.getAnswer().get(i);
                                            cardArrayList.add(new String[]{record.get("title"), record.get("authors"), record.get("publisher"), record.get("publishedDate"), record.get("description"), record.get("thumbnail"), record.get("ISBN")});


                                            mCustomCardAdapter = new SearchCardAdapter(requireContext(), cardArrayList);
                                            booksListView.setAdapter(mCustomCardAdapter);
                                            booksListView.setVisibility(View.VISIBLE);
                                            booksListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                                @Override
                                                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                                    Log.i("success", "Clicked on:" + cardArrayList.get(i)[0]);
                                                    Intent intent = new Intent(getActivity().getApplication(), BookInfo.class);
                                                    intent.putExtra("bookData", cardArrayList.get(i));
                                                    startActivity(intent);


                                                }
                                            });
                                        }
                                    }
                                } else {
                                    Log.i("Success Checking", "" + mResult.getError());
                                }
                            }

                            @Override
                            public void onFailure(Call<SearchBookResult> call, Throwable t) {
                                Toast.makeText(getContext(), "Unable to fetch results from server", Toast.LENGTH_SHORT).show();
                                Log.i("Failure Checking", "There was an error " + t.getMessage());
                            }
                        });
                    } catch (Exception e) {
                        Toast.makeText(getContext(), "Unable to fetch results from server", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                }
            }
        });

        // Inflate the layout for this fragment
        return view;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }




}