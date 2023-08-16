package com.example.hackathoneonebite.main.fragment

import android.content.Context
import android.content.Intent
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
import com.example.hackathoneonebite.api.LoadRanking
import com.example.hackathoneonebite.api.RetrofitBuilder
import com.example.hackathoneonebite.databinding.FragmentMain4RankingTopBinding
import com.example.hackathoneonebite.main.MainFrameActivity
import com.example.hackathoneonebite.main.ProfileActivity
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Main4RankingFragmentTop : Fragment() {
    //등수, 프사, 이름, 점수
    private lateinit var viewModel: Main4RankingViewModel
    lateinit var binding : FragmentMain4RankingTopBinding
    lateinit var adapter: AdapterMain4Ranking
    var data_rank: MutableList<LoadRanking> = arrayListOf()
    var id: Long = 0
    var userId: String = ""
    val baseUrl = "http://203.252.139.231:8080/"
    override fun onAttach(context: Context) {
        super.onAttach(context)
        val activity = requireActivity() as MainFrameActivity
        this.id = activity.id
        this.userId = activity.userId
    }
    private fun startProfileActivity(targetId: Long) {
        val i = Intent(requireContext(), ProfileActivity::class.java)
        i.putExtra("requesterId",this.id)
        i.putExtra("targetId",targetId)
        startActivity(i)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMain4RankingTopBinding.inflate(layoutInflater, container, false)
        viewModel = ViewModelProvider(requireActivity()).get(Main4RankingViewModel::class.java)

        binding.rankRecyclerView.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false)
        adapter = AdapterMain4Ranking(requireContext(), data_rank)
        adapter.itemClickListener = object: AdapterMain4Ranking.OnItemClickListener {
            override fun OnItemClick(position: Int) {
                startProfileActivity(adapter.dataList[position].userId)
            }
        }
        binding.rankRecyclerView.adapter = adapter
        binding.myRanking.setOnClickListener {
            startProfileActivity(this.id)
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
        loadRanking()
    }

    private fun init() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            loadRanking()
        }
    }

    private fun applyRanking() {
        if (isAdded) {
            if (data_rank.count() > 0) {
                Glide.with(requireContext())
                    .load(baseUrl + data_rank[0].profileUrl)
                    .into(binding.rankFirst)
                binding.rankFirst.setOnClickListener {
                    startProfileActivity(data_rank[0].userId)
                }
            }
            if (data_rank.count() > 1) {
                Glide.with(requireContext())
                    .load(baseUrl + data_rank[1].profileUrl)
                    .into(binding.rankSecond)
                binding.rankSecond.setOnClickListener {
                    startProfileActivity(data_rank[1].userId)
                }
            }
            if (data_rank.count() > 2) {
                Glide.with(requireContext())
                    .load(baseUrl + data_rank[2].profileUrl)
                    .into(binding.rankThird)
                binding.rankThird.setOnClickListener {
                    startProfileActivity(data_rank[2].userId)
                }
            }
            //자기 랭킹 표시
            for (i in 0..data_rank.count() - 1) {
                if (data_rank[i].userId == this.id) {
                    binding.myRankText.text = (i + 1).toString()
                    Glide.with(requireContext())
                        .load(baseUrl + data_rank[i].profileUrl)
                        .into(binding.myRankingProfile)
                    binding.myRankName.text = data_rank[i].username
                    binding.myScore.text = data_rank[i].score.toString()
                    break
                }
            }
        }
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
                        binding.swipeRefreshLayout.isRefreshing = false
                        Log.e("MAIN4RANKING_CREATE_COMMENT2", "로드 되지 않았습니다.")
                        return
                    } else  {
                        adapter.dataList.clear()
                        for (i in 0..commentResponse.count()-1) {
                            adapter.dataList.add(commentResponse[i])
                            Log.d("main4444", adapter.dataList.toString())
                        }
                        binding.swipeRefreshLayout.isRefreshing = false
                        adapter.notifyDataSetChanged()
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
                            binding.swipeRefreshLayout.isRefreshing = false
                            Toast.makeText(requireContext(), "로드 되지 않았습니다.", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        binding.swipeRefreshLayout.isRefreshing = false
                        Toast.makeText(requireContext(), "로드 되지 않았습니다.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            override fun onFailure(call: Call<List<LoadRanking>>, t: Throwable) {
                // 통신에 실패한 경우
                Log.d("MAIN4RANKING CONNECTION FAILURE_CREATE_COMMENT4: ", t.localizedMessage)
                binding.swipeRefreshLayout.isRefreshing = false
                Toast.makeText(requireContext(), "로드 되지 않았습니다.", Toast.LENGTH_SHORT).show()
            }
        })
    }
}