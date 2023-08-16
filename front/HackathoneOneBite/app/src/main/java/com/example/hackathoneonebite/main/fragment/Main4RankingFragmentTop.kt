package com.example.hackathoneonebite.main.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.hackathoneonebite.Data.Rank
import com.example.hackathoneonebite.R
import com.example.hackathoneonebite.api.CommentResponse
import com.example.hackathoneonebite.api.LoadRanking
import com.example.hackathoneonebite.api.RetrofitBuilder
import com.example.hackathoneonebite.databinding.FragmentMain4RankingBottomBinding
import com.example.hackathoneonebite.databinding.FragmentMain4RankingTopBinding
import com.example.hackathoneonebite.main.MainFrameActivity
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Random

class Main4RankingFragmentTop : Fragment() {
    //등수, 프사, 이름, 점수
    private lateinit var viewModel: Main4RankingViewModel
    lateinit var binding : FragmentMain4RankingTopBinding
    var data_rank: List<LoadRanking> = arrayListOf()
    var id: Long = 0
    var userId: String = ""
    val baseUrl = "http://203.252.139.231:8080/"
    override fun onAttach(context: Context) {
        super.onAttach(context)
        val activity = requireActivity() as MainFrameActivity
        this.id = activity.id
        this.userId = activity.userId
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMain4RankingTopBinding.inflate(layoutInflater, container, false)
        viewModel = ViewModelProvider(requireActivity()).get(Main4RankingViewModel::class.java)

        val recyclerView = binding.rankRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        val adapter = AdapterMain4Ranking(data_rank)
        recyclerView.adapter = adapter

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadRanking()
    }

    private fun applyRanking() {
        Glide.with(requireContext())
            .load(baseUrl+data_rank[0].profileUrl)
            .into(binding.rankFirst)
        Glide.with(requireContext())
            .load(baseUrl+data_rank[0].profileUrl)
            .into(binding.rankSecond)
        Glide.with(requireContext())
            .load(baseUrl+data_rank[0].profileUrl)
            .into(binding.rankThird)
    }
    private fun loadRanking() {
        loadRankingRequest()
    }
    private fun loadRankingRequest() {
        val call = RetrofitBuilder.api.main4Ranking()
        call.enqueue(object : Callback<List<LoadRanking>> { // 비동기 방식 통신 메소드
            override fun onResponse(
                call: Call<List<LoadRanking>>,
                response: Response<List<LoadRanking>>
            ) {
                Log.e("MAIN4RANKING_COMMENT1", response.raw().request.url.toString())
                if(response.isSuccessful()){ // 응답 잘 받은 경우
                    val commentResponse  = response.body()
                    if (commentResponse == null) {
                        Log.e("MAIN4RANKING_CREATE_COMMENT2", "응답이 null입니다.")
                        return
                    } else  {
                        data_rank = commentResponse
                        applyRanking()
                    }
                } else {
                    // 통신 성공 but 응답 실패
                    val errorBody = response.errorBody()?.string()
                    if (!errorBody.isNullOrEmpty()) {
                        try {
                            val jsonObject = JSONObject(errorBody)
                            val errorMessage = jsonObject.getString("error_message")
                            Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
                        } catch (e: JSONException) {
                            Log.e("MAIN4RANKING_CREATE_COMMENT3", "Failed to parse error response: $errorBody")
                            Toast.makeText(requireContext(), "오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(requireContext(), "오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            override fun onFailure(call: Call<List<LoadRanking>>, t: Throwable) {
                // 통신에 실패한 경우
                Log.d("MAIN4RANKING CONNECTION FAILURE_CREATE_COMMENT4: ", t.localizedMessage)
                Toast.makeText(requireContext(), "서버와의 통신에 문제가 있습니다.", Toast.LENGTH_SHORT).show()
            }
        })
    }
}