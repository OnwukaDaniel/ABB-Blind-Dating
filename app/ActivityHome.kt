package com.iodaniel.abb

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.GravityCompat
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.gson.Gson
import com.iodaniel.abb.data_class.PersonData
import com.iodaniel.abb.databinding.ActivityHomeBinding
import com.iodaniel.abb.home_fragments.*
import com.iodaniel.abb.liveData.ProfileLiveData
import com.iodaniel.abb.notification.FragmentNotification
import com.iodaniel.abb.registration.ActivityLanding
import com.iodaniel.abb.util.ChildEventTemplate
import com.iodaniel.abb.viewModel.HomeTabSelector
import com.iodaniel.abb.viewModel.HomeViewModel
import com.iodaniel.abb.viewModel.ProfileViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ActivityHome : AppCompatActivity(), View.OnClickListener {
    private val binding by lazy { ActivityHomeBinding.inflate(layoutInflater) }
    private val homeViewModel = HomeViewModel()
    private val scope = CoroutineScope(Dispatchers.IO)
    private var pref: SharedPreferences? = null
    private var currentTab = 0
    private val storyAdapter = StoryAdapter()
    private val favProfileDataset: ArrayList<PersonData> = arrayListOf()
    private val profileViewModel: ProfileViewModel by viewModels()
    private lateinit var profileLiveData: ProfileLiveData

    private var ref = FirebaseDatabase.getInstance().reference
    private var dataset: ArrayList<PersonData> = arrayListOf()
    private val dataKeyList: ArrayList<String> = arrayListOf()

    private var refPersonalData = FirebaseDatabase.getInstance().reference
    private var refFav = FirebaseDatabase.getInstance().reference
    private var auth = FirebaseAuth.getInstance().currentUser
    private lateinit var favLiveData: ProfileLiveData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        pref = getSharedPreferences(getString(R.string.ALL_PREF), Context.MODE_PRIVATE)
        binding.homeLogOut.setOnClickListener(this)
        supportFragmentManager.beginTransaction().replace(R.id.home_frame, FragmentDiscover()).commit()
        homeViewModel.setTab(HomeTabSelector.discover)
        binding.homeBottomNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.discover -> {
                    supportFragmentManager.beginTransaction()
                        .setCustomAnimations(R.anim.enter_left_to_right,
                            R.anim.exit_left_to_right,
                            R.anim.enter_right_to_left,
                            R.anim.exit_right_to_left)
                        .replace(R.id.home_frame, FragmentDiscover())
                        .commit()
                    homeViewModel.setTab(HomeTabSelector.discover)
                    return@setOnItemSelectedListener true
                }
                R.id.fav -> {
                    supportFragmentManager.beginTransaction()
                        .setCustomAnimations(R.anim.enter_right_to_left,
                            R.anim.exit_right_to_left,
                            R.anim.enter_left_to_right,
                            R.anim.exit_left_to_right)
                        .replace(R.id.home_frame, FragmentFav())
                        .commit()
                    homeViewModel.setTab(HomeTabSelector.fav)
                    return@setOnItemSelectedListener true
                }
                R.id.update -> {
                    supportFragmentManager.beginTransaction().addToBackStack("update")
                        .setCustomAnimations(R.anim.enter_right_to_left,
                            R.anim.exit_right_to_left,
                            R.anim.enter_left_to_right,
                            R.anim.exit_left_to_right)
                        .replace(R.id.home_root, FragmentUpdate())
                        .commit()
                    homeViewModel.setTab(HomeTabSelector.update)
                    return@setOnItemSelectedListener true
                }
                R.id.msgs -> {
                    supportFragmentManager.beginTransaction()
                        .setCustomAnimations(R.anim.enter_right_to_left,
                            R.anim.exit_right_to_left,
                            R.anim.enter_left_to_right,
                            R.anim.exit_left_to_right)
                        .replace(R.id.home_frame, FragmentMsgs())
                        .commit()
                    homeViewModel.setTab(HomeTabSelector.msg)
                    return@setOnItemSelectedListener true
                }
                R.id.profile -> {
                    supportFragmentManager.beginTransaction()
                        .setCustomAnimations(R.anim.enter_right_to_left,
                            R.anim.exit_right_to_left,
                            R.anim.enter_left_to_right,
                            R.anim.exit_left_to_right)
                        .replace(R.id.home_frame, FragmentMyProfile())
                        .commit()
                    homeViewModel.setTab(HomeTabSelector.profile)
                    return@setOnItemSelectedListener true
                }
            }
            return@setOnItemSelectedListener false
        }

        binding.homeBottomNav.setOnItemReselectedListener {
            when (it.itemId) {
                R.id.discover -> {
                }
                R.id.fav -> {
                }
                R.id.update -> {
                }
                R.id.msgs -> {
                }
                R.id.profile -> {
                }
            }
        }

        homeViewModel.tab.observe(this) {
            when (it) {
                HomeTabSelector.discover -> {
                    currentTab = 0
                    binding.homeRtl.setImageResource(R.drawable.notification)
                    binding.homeRtl.setPadding(0, 0, 0, 0)
                    binding.homeRtlCard.setOnClickListener {
                        supportFragmentManager.beginTransaction().addToBackStack("notification")
                            .setCustomAnimations(R.anim.enter_right_to_left,
                                R.anim.exit_right_to_left,
                                R.anim.enter_left_to_right,
                                R.anim.exit_left_to_right)
                            .replace(R.id.home_root, FragmentNotification())
                            .commit()
                    }
                    unReset()
                }
                HomeTabSelector.fav -> {
                    currentTab = 1
                    binding.homeRtl.setImageResource(R.drawable.ic_search)
                    binding.homeRtl.setPadding(0, 0, 0, 0)
                    binding.homeRtl.setPadding(4, 4, 4, 4)
                    binding.homeRtlCard.setOnClickListener {
                        supportFragmentManager.beginTransaction().addToBackStack("search")
                            .setCustomAnimations(R.anim.enter_top_to_bottom,
                                R.anim.exit_top_to_bottom,
                                R.anim.enter_bottom_to_top,
                                R.anim.exit_bottom_to_top)
                            .replace(R.id.home_root, FragmentSearch())
                            .commit()
                    }
                }
                HomeTabSelector.update -> {
                    currentTab = 2
                    //binding.homeRtl.setBackgroundResource(R.drawable.ic_search)
                    reset()
                }
                HomeTabSelector.msg -> {
                    currentTab = 3
                    binding.homeRtl.setPadding(0, 0, 0, 0)
                    binding.homeRtl.setImageResource(R.drawable.notification)
                    binding.homeRtlCard.setOnClickListener {
                        supportFragmentManager.beginTransaction().addToBackStack("notification")
                            .setCustomAnimations(R.anim.enter_right_to_left,
                                R.anim.exit_right_to_left,
                                R.anim.enter_left_to_right,
                                R.anim.exit_left_to_right)
                            .replace(R.id.home_root, FragmentNotification())
                            .commit()
                    }
                    unReset()
                }
                HomeTabSelector.profile -> {
                    currentTab = 4
                    binding.homeRtl.setImageResource(R.drawable.edit)
                    binding.homeRtlCard.setOnClickListener {
                    }
                    binding.homeRtl.setPadding(0, 0, 0, 0)
                    binding.homeRtl.setPadding(18, 18, 18, 18)
                    unReset()
                }
            }
        }
        binding.homeUpdateFab.setOnClickListener(this)
    }

    override fun onStart() {
        super.onStart()
        ref = ref.child(getString(R.string.profile))
        profileLiveData = ProfileLiveData(ref)
        profileLiveData.observe(this) {
            when (it.second) {
                ChildEventTemplate.onDataChange -> {
                    var d: ArrayList<PersonData> = arrayListOf()
                    for (i in it.first.children) {
                        val personData = i.getValue(PersonData::class.java)!!
                        if (personData.emailPasswordPair["email"].toString().trim() == auth!!.email.toString().trim()) {
                            profileViewModel.setPersonData(personData)
                            continue
                        }
                        d.add(personData)
                    }
                    profileViewModel.setPersonDataList(d)
                    if (d.size > 50) d = d.subList(0, 50) as ArrayList<PersonData>
                    d.shuffle()
                    for (i in d) {
                        if (dataKeyList.contains(i.auth)) continue
                        dataset.add(i)
                        dataKeyList.add(i.auth)
                    }
                    profileViewModel.setPersonDataList(dataset)
                }
            }
        }

        val setMyData = false
        refPersonalData = refPersonalData.child(getString(R.string.profile)).child(auth!!.uid)
        profileLiveData = ProfileLiveData(refPersonalData)
        profileLiveData.observe(this){
            when(it.second){
                ChildEventTemplate.onDataChange->{
                    val personData = it.first.getValue(PersonData::class.java)!!
                    if (setMyData) scope.launch {
                        delay(30_000)
                        val json = Gson().toJson(personData)
                        pref!!.edit().putString(getString(R.string.user_profile), json).apply()
                    } else {
                        val json = Gson().toJson(personData)
                        pref!!.edit().putString(getString(R.string.user_profile), json).apply()
                    }
                }
            }
        }

        refFav = refFav.child("favorite").child(auth!!.uid)
        favLiveData = ProfileLiveData(refFav)
        favLiveData.observe(this) {
            val json = Gson().toJson(it.first.value!!)
            val snap: ArrayList<*> = Gson().fromJson(json, ArrayList::class.java)
            favProfileDataset.clear()
            for (i in snap) {
                val gson = Gson().toJson(i)
                val data: PersonData = Gson().fromJson(gson, PersonData::class.java)
                favProfileDataset.add(data)
            }
            profileViewModel.setFavData(favProfileDataset)
        }
    }

    override fun onResume() {
        super.onResume()
        val actionBarToggle = ActionBarDrawerToggle(this, binding.homeDrawerRoot, R.string.open_drawer, R.string.close_drawer)
        binding.homeDrawerRoot.addDrawerListener(actionBarToggle)
        actionBarToggle.syncState()
        actionBarToggle.setHomeAsUpIndicator(R.drawable.menu)

        actionBarToggle.isDrawerIndicatorEnabled = false
        val drawable = ResourcesCompat.getDrawable(resources, R.drawable.menu, theme)
        actionBarToggle.setHomeAsUpIndicator(drawable)
        binding.homeLtlCard.setOnClickListener {
            if (binding.homeDrawerRoot.isDrawerVisible(GravityCompat.START)) {
                binding.homeDrawerRoot.closeDrawer(GravityCompat.START)
            } else {
                binding.homeDrawerRoot.openDrawer(GravityCompat.START)
            }
        }
        /*binding.homeNavigation.setNavigationItemSelectedListener {
            when(it.itemId){
                R.id.nav_fav -> {
                    if (currentTab == 1) {
                        binding.homeDrawerRoot.closeDrawer(GravityCompat.START)
                        return@setNavigationItemSelectedListener false
                    }
                    scope.launch {
                        binding.homeDrawerRoot.closeDrawer(GravityCompat.START)
                        delay(500)
                        runOnUiThread {
                            supportFragmentManager.beginTransaction()
                                .setCustomAnimations(R.anim.enter_right_to_left,
                                    R.anim.exit_right_to_left,
                                    R.anim.enter_left_to_right,
                                    R.anim.exit_left_to_right)
                                .replace(R.id.home_frame, FragmentFav())
                                .commit()
                            homeViewModel.setTab(HomeTabSelector.fav)
                        }
                    }
                    currentTab = 1

                    return@setNavigationItemSelectedListener true
                }
                R.id.nav_msgs -> {
                    if (currentTab == 3) {
                        binding.homeDrawerRoot.closeDrawer(GravityCompat.START)
                        return@setNavigationItemSelectedListener false
                    }
                    scope.launch {
                        binding.homeDrawerRoot.closeDrawer(GravityCompat.START)
                        delay(500)
                        runOnUiThread {
                            supportFragmentManager.beginTransaction()
                                .setCustomAnimations(R.anim.enter_right_to_left,
                                    R.anim.exit_right_to_left,
                                    R.anim.enter_left_to_right,
                                    R.anim.exit_left_to_right)
                                .replace(R.id.home_frame, FragmentMsgs())
                                .commit()
                            homeViewModel.setTab(HomeTabSelector.msg)
                        }
                    }
                    currentTab = 3
                    return@setNavigationItemSelectedListener true
                }
                R.id.nav_profile -> {
                    if (currentTab == 4) {
                        binding.homeDrawerRoot.closeDrawer(GravityCompat.START)
                        return@setNavigationItemSelectedListener false
                    }
                    currentTab = 4
                    scope.launch {
                        binding.homeDrawerRoot.closeDrawer(GravityCompat.START)
                        delay(500)
                        runOnUiThread{
                            supportFragmentManager.beginTransaction()
                                .setCustomAnimations(R.anim.enter_right_to_left,
                                    R.anim.exit_right_to_left,
                                    R.anim.enter_left_to_right,
                                    R.anim.exit_left_to_right)
                                .replace(R.id.home_frame, FragmentMyProfile())
                                .commit()
                            homeViewModel.setTab(HomeTabSelector.profile)
                        }
                    }
                    return@setNavigationItemSelectedListener true
                }
            }
            return@setNavigationItemSelectedListener false
        }*/
    }

    private fun reset() {
        binding.homeRtlCard.visibility = View.INVISIBLE
    }

    private fun unReset() {
        binding.homeRtlCard.visibility = View.VISIBLE
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.home_update_fab -> {
                supportFragmentManager.beginTransaction().addToBackStack("update").replace(R.id.home_root, FragmentUpdate())
                    .setCustomAnimations(R.anim.enter_right_to_left, R.anim.exit_right_to_left)
                    .commit()
                homeViewModel.setTab(HomeTabSelector.update)
            }
            R.id.home_log_out->{
                scope.launch {
                    binding.homeDrawerRoot.closeDrawer(GravityCompat.START)
                    delay(500)
                    FirebaseAuth.getInstance().signOut()
                    val intent = Intent(applicationContext, ActivityLanding::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    overridePendingTransition(R.anim.enter_right_to_left, R.anim.exit_right_to_left)
                }
            }
        }
    }
}