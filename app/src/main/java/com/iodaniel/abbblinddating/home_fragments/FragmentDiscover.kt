package com.iodaniel.abbblinddating.home_fragments

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Resources
import android.graphics.Rect
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.gson.Gson
import com.iodaniel.abbblinddating.R
import com.iodaniel.abbblinddating.data_class.PersonData
import com.iodaniel.abbblinddating.data_class.StatusData
import com.iodaniel.abbblinddating.databinding.DiscoverRowBinding
import com.iodaniel.abbblinddating.databinding.FragmentDiscoverBinding
import com.iodaniel.abbblinddating.liveData.ProfileLiveData
import com.iodaniel.abbblinddating.viewModel.ProfileViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs

class FragmentDiscover : Fragment() {
    private lateinit var binding: FragmentDiscoverBinding
    private var viewPagerAdapter: ViewPagerAdapter? = null
    private val itemDecoration = HorizontalMarginDecoration()
    private lateinit var pref: SharedPreferences
    private val scope = CoroutineScope(Dispatchers.IO)
    private var dataset: ArrayList<PersonData> = arrayListOf()
    private val dataKeyList: ArrayList<String> = arrayListOf()
    private val storyAdapter = StoryAdapter()
    private var favProfileDataset: ArrayList<PersonData> = arrayListOf()
    private val profileViewModel: ProfileViewModel by activityViewModels()

    private var ref = FirebaseDatabase.getInstance().reference
    private var refFav = FirebaseDatabase.getInstance().reference
    private var auth = FirebaseAuth.getInstance().currentUser
    private lateinit var profileLiveData: ProfileLiveData
    private lateinit var favLiveData: ProfileLiveData

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentDiscoverBinding.inflate(inflater, container, false)
        pref = requireActivity().getSharedPreferences(getString(R.string.ALL_PREF), Context.MODE_PRIVATE)

        viewPagerAdapter = ViewPagerAdapter(this)
        binding.discoverStack.adapter = viewPagerAdapter

        binding.rvStory.adapter = storyAdapter
        storyAdapter.dataset = arrayListOf(StatusData(empty = true))
        binding.rvStory.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        ref = ref.child(getString(R.string.profile))
        refFav = refFav.child(getString(R.string.favorite)).child(auth!!.uid)
        favLiveData = ProfileLiveData(refFav)
        profileLiveData = ProfileLiveData(ref)
        profileViewModel.personDataList.observe(viewLifecycleOwner) {
            for ((index, i) in it.withIndex()) {
                if (dataKeyList.contains(i.auth)) continue
                dataset.add(i)
                dataKeyList.add(i.auth)
                binding.discoverNoRegisteredUser.visibility = View.GONE
                binding.discoverRegisteredUser.visibility = View.VISIBLE
                viewPagerAdapter!!.notifyItemInserted(index)
            }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ref.get().addOnSuccessListener {
            binding.discoverMessage.text = getString(R.string.connecting_to_others)
        }.addOnFailureListener {
            scope.launch {
                delay(5_000)
                try {
                    val errorMsg = getString(R.string.network_error_connect)
                    requireActivity().runOnUiThread { binding.discoverMessage.text = errorMsg }
                } catch (e: java.lang.Exception) {
                }
            }
        }

        profileViewModel.favData.observe(viewLifecycleOwner) {
            if (it != null) favProfileDataset = it
        }
        // PAGE TRANSFORMER
        binding.discoverStack.offscreenPageLimit = 20
        val currentMarginPx = 224
        val nextMarginPx = 6
        val pageTranslationX = currentMarginPx + nextMarginPx
        val pageTransformer = ViewPager2.PageTransformer { page, position ->
            page.translationX = -pageTranslationX * position
            // Next line scales the item's height. You can remove it if you don't want this effect
            page.scaleY = 1 - (0.25f * abs(position))
            // If you want a fading effect uncomment the next line:
            page.alpha = 0.25f + (1 - abs(position))
        }
        binding.discoverStack.setPageTransformer(VerticalStackTransformer(20))
        binding.discoverStack.addItemDecoration(itemDecoration)
        //runBlocking { scope.launch { binding.discoverStack.setCurrentItem(2, true) } }

        binding.discoverFav.setOnClickListener {
            val datum = dataset[binding.discoverStack.currentItem]
            if (!favProfileDataset.contains(datum)) {
                favProfileDataset.add(datum)
                refFav.setValue(favProfileDataset).addOnSuccessListener {
                    Toast.makeText(requireContext(), getString(R.string.added_to_favorite), Toast.LENGTH_LONG).show()
                }.addOnFailureListener {
                    Toast.makeText(requireContext(), getString(R.string.adding_to_fav_error), Toast.LENGTH_LONG).show()
                }
            }
        }
        binding.discoverCancel.setOnClickListener {
            try {
                viewPagerAdapter!!.removeFragment(binding.discoverStack.currentItem)
            } catch (e: Exception) {
            }
        }
        if (dataset.isEmpty()) {
            binding.discoverRegisteredUser.visibility = View.GONE
            binding.discoverNoRegisteredUser.visibility = View.VISIBLE
        } else {
            binding.discoverNoRegisteredUser.visibility = View.GONE
            binding.discoverRegisteredUser.visibility = View.VISIBLE
        }
    }

    inner class ViewPagerAdapter(fa: Fragment) : FragmentStateAdapter(fa) {
        private val pageIds = dataset.map { it.hashCode().toLong() }

        override fun getItemCount() = dataset.size

        override fun createFragment(position: Int): Fragment {

            val datum = dataset[position]
            val fragment = DiscoverFragment()
            val b = Bundle()
            val json = Gson().toJson(datum)
            b.putString("json", json)
            fragment.arguments = b
            return fragment
        }

        fun removeFragment(position: Int) {
            dataset.removeAt(position)
            notifyItemRangeChanged(position, dataset.size)
            notifyDataSetChanged()
        }

        override fun getItemId(position: Int): Long {
            return dataset[position].hashCode().toLong() // make sure notifyDataSetChanged() works
        }

        override fun containsItem(itemId: Long): Boolean {
            return pageIds.contains(itemId)
        }
    }
}

class HorizontalMarginDecoration : RecyclerView.ItemDecoration() {
    private val horizontalMarginInPx: Int = 24
    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)
        outRect.right = horizontalMarginInPx
        outRect.left = horizontalMarginInPx
    }
}

class VerticalStackTransformer(private val offscreenPageLimit: Int) : ViewPager2.PageTransformer {

    companion object {
        private const val DEFAULT_TRANSLATION_Y = .0f

        private const val SCALE_FACTOR = .12f
        private const val DEFAULT_SCALE = 1f

        private const val ALPHA_FACTOR = .5f
        private const val DEFAULT_ALPHA = 1f
    }

    override fun transformPage(page: View, position: Float) {
        page.apply {
            ViewCompat.setElevation(page, -Math.abs(position))

            val scaleFactor = -SCALE_FACTOR * position + DEFAULT_SCALE
            val alphaFactor = -ALPHA_FACTOR * position + DEFAULT_ALPHA

            when {
                position <= 0f -> {
                    translationY = DEFAULT_TRANSLATION_Y
                    scaleX = DEFAULT_SCALE
                    scaleY = DEFAULT_SCALE
                    //alpha = DEFAULT_ALPHA + position
                }
                position <= offscreenPageLimit - 1 -> {
                    scaleX = scaleFactor
                    scaleY = DEFAULT_SCALE
                    translationY = (-height * position + 10.dpToPx * position)
                    alpha = alphaFactor
                }
                else -> {
                    translationY = DEFAULT_TRANSLATION_Y
                    scaleX = DEFAULT_SCALE
                    scaleY = DEFAULT_SCALE
                    alpha = alphaFactor
                }
            }
        }
    }

    val Int.dpToPx: Int get() = (this * Resources.getSystem().displayMetrics.density).toInt()
}

class StoryAdapter : RecyclerView.Adapter<StoryAdapter.StoryAdapterViewHolder>() {

    var dataset: ArrayList<StatusData> = arrayListOf()
    private lateinit var context: Context

    class StoryAdapterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.story_user_name)
        val image: ImageView = itemView.findViewById(R.id.story_user_image)
        val plus: ImageView = itemView.findViewById(R.id.story_plus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryAdapterViewHolder {
        context = parent.context
        val view = LayoutInflater.from(context).inflate(R.layout.story_row, parent, false)
        return StoryAdapterViewHolder(view)
    }

    override fun onBindViewHolder(holder: StoryAdapterViewHolder, position: Int) {
        val datum = dataset[position]
        if (datum.lastMedia != "") Glide.with(context).load(Uri.parse(datum.lastMedia)).centerCrop().into(holder.image)
        if (datum.empty) holder.plus.visibility = View.VISIBLE
    }

    override fun getItemCount() = dataset.size
}

class DiscoverFragment : Fragment() {
    private lateinit var binding: DiscoverRowBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DiscoverRowBinding.inflate(inflater, container, false)

        val json = requireArguments().getString("json")
        val personData: PersonData = Gson().fromJson(json, PersonData::class.java)
        binding.stackName.text = personData.name
        binding.stackImage.setOnClickListener {
            val intent = Intent(context, ActivityOtherProfile::class.java)
            intent.putExtra(getString(R.string.profile), json)
            requireContext().startActivity(intent)
            requireActivity().overridePendingTransition(R.anim.enter_right_to_left, R.anim.exit_right_to_left)
        }
        binding.stackLocation.text = personData.location
        Glide.with(requireContext()).load(personData.image).centerCrop().into(binding.stackImage)
        return binding.root
    }
}