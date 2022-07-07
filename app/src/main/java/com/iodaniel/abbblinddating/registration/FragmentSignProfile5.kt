package com.iodaniel.abbblinddating.registration

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import android.widget.TextView
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.iodaniel.abbblinddating.R
import com.iodaniel.abbblinddating.data_class.PersonData
import com.iodaniel.abbblinddating.databinding.FragmentSignProfile5Binding
import com.iodaniel.abbblinddating.util.ImageCompressor.compressImage
import com.iodaniel.abbblinddating.util.Keyboard.hideKeyboard
import com.iodaniel.abbblinddating.viewModel.SignUpViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*

class FragmentSignProfile5 : Fragment(), View.OnClickListener {
    private lateinit var binding: FragmentSignProfile5Binding
    private val languagesAdapter = LanguagesAdapter()
    private val personData = PersonData()
    private var auth = FirebaseAuth.getInstance().currentUser
    private var ref = FirebaseDatabase.getInstance().reference
    private val signUpViewModel: SignUpViewModel by activityViewModels()
    private var success: Boolean = false
    private lateinit var task: Task<Void>
    private var isVerified = false
    private val scope = CoroutineScope(Dispatchers.IO)
    private var gender: ArrayList<String> = arrayListOf()
    private var hairColor: ArrayList<String> = arrayListOf()
    private val acceptedImages: ArrayList<String> = arrayListOf("jpg", "png", "JPEG")
    private var byteIS: ByteArray? = null

    private val pickFileLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult(), ActivityResultCallback {
        try {
            if (it.data!!.data == null) return@ActivityResultCallback
            if (it.resultCode == AppCompatActivity.RESULT_OK) {
                val dataUri = it.data!!.data
                val contentResolver = requireActivity().contentResolver
                val mime = MimeTypeMap.getSingleton()
                val ext = mime.getExtensionFromMimeType(contentResolver?.getType(dataUri!!))!!

                if (ext !in acceptedImages) return@ActivityResultCallback

                val lastPathSegment = dataUri!!.lastPathSegment!!
                //val name = lastPathSegment.substring(lastPathSegment.lastIndexOf("/") + 1)
                if (ext in acceptedImages) {

                    val pair = compressImage(dataUri, requireContext(), null)
                    byteIS = pair.second
                    Glide.with(requireContext()).load(byteIS).centerCrop().into(binding.profile5Image)
                }
            }
        } catch (e: Exception) {
            Snackbar.make(binding.root, "${e.localizedMessage}", Snackbar.LENGTH_LONG).show()
        }
    })

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentSignProfile5Binding.inflate(inflater, container, false)
        gender= arrayListOf(getString(R.string.male), getString(R.string.female))
        hairColor= arrayListOf(getString(R.string.black_color), getString(R.string.brown), getString(R.string.blond_hair), getString(R.string.red), getString(R.string.grey), getString(R.string.brunette))
        binding.profile5Back.setOnClickListener(this)
        binding.profile5Continue.setOnClickListener(this)
        binding.profile5PickImage.setOnClickListener(this)

        binding.profile5Male.setOnClickListener(this)
        binding.profile5Female.setOnClickListener(this)

        binding.profile5Black.setOnClickListener(this)
        binding.profile5Brown.setOnClickListener(this)
        binding.profile5Blond.setOnClickListener(this)
        binding.profile5Red.setOnClickListener(this)
        binding.profile5Grey.setOnClickListener(this)
        binding.profile5Brunette.setOnClickListener(this)

        return binding.root
    }

    private fun clearHairColor() {
        binding.profile5Black.setBackgroundResource(R.drawable.rounded_unselected)
        binding.profile5Brown.setBackgroundResource(R.drawable.rounded_unselected)
        binding.profile5Blond.setBackgroundResource(R.drawable.rounded_unselected)
        binding.profile5Red.setBackgroundResource(R.drawable.rounded_unselected)
        binding.profile5Grey.setBackgroundResource(R.drawable.rounded_unselected)
        binding.profile5Brunette.setBackgroundResource(R.drawable.rounded_unselected)
    }

    private fun clearGender() {
        binding.profile5Male.setBackgroundResource(R.drawable.rounded_unselected)
        binding.profile5Female.setBackgroundResource(R.drawable.rounded_unselected)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        task = auth!!.reload()
        task.addOnCompleteListener {
            if (it.isSuccessful) {
                isVerified = auth!!.isEmailVerified
            } else {
                Snackbar.make(binding.root, getString(R.string.check_your_email_for_verification_link), Snackbar.LENGTH_LONG).show()
            }
        }.addOnFailureListener {
            Snackbar.make(binding.root, "${it.localizedMessage}", Snackbar.LENGTH_LONG).show()
        }
        languagesAdapter.signUpViewModel = signUpViewModel
        languagesAdapter.activity = requireActivity()
        languagesAdapter.selected = personData.language
        languagesAdapter.dataset = arrayListOf(getString(R.string.english), getString(R.string.korean),
            getString(R.string.japanese), getString(R.string.chinese),
            getString(R.string.deutsch), getString(R.string.hebrew), getString(R.string.italian),
            getString(R.string.portuguese), getString(R.string.others))
        binding.profile5Rv.adapter = languagesAdapter
        binding.profile5Rv.layoutManager = StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.HORIZONTAL)
        signUpViewModel.hairColor.observe(viewLifecycleOwner) {
            personData.hairColorChoice = it
            clearHairColor()
            when (it) {
                hairColor[0] -> binding.profile5Black.setBackgroundResource(R.drawable.rounded_selected)
                hairColor[1] -> binding.profile5Brown.setBackgroundResource(R.drawable.rounded_selected)
                hairColor[2] -> binding.profile5Blond.setBackgroundResource(R.drawable.rounded_selected)
                hairColor[3] -> binding.profile5Red.setBackgroundResource(R.drawable.rounded_selected)
                hairColor[4] -> binding.profile5Grey.setBackgroundResource(R.drawable.rounded_selected)
                hairColor[5] -> binding.profile5Brunette.setBackgroundResource(R.drawable.rounded_selected)
            }
        }
        signUpViewModel.gender.observe(viewLifecycleOwner) {
            personData.genderChoice = it
            clearGender()
            when (it) {
                gender[0] -> binding.profile5Male.setBackgroundResource(R.drawable.rounded_selected)
                gender[1] -> binding.profile5Female.setBackgroundResource(R.drawable.rounded_selected)
            }
        }
        signUpViewModel.interests.observe(viewLifecycleOwner) {
            personData.interests = it
        }
        signUpViewModel.language.observe(viewLifecycleOwner) {
            personData.language = it
        }
        signUpViewModel.maritalStatus.observe(viewLifecycleOwner) {
            personData.maritalStatus = it
        }
        signUpViewModel.bodyStyle.observe(viewLifecycleOwner) {
            personData.bodyStyle = it
        }
        signUpViewModel.dateSmoker.observe(viewLifecycleOwner) {
            personData.dateSmoker = it
        }
        signUpViewModel.about.observe(viewLifecycleOwner) {
            personData.about = it
        }
        signUpViewModel.education.observe(viewLifecycleOwner) {
            personData.education = it
        }
        signUpViewModel.emailPasswordPair.observe(viewLifecycleOwner) {
            personData.emailPasswordPair = it
        }
        signUpViewModel.birthday.observe(viewLifecycleOwner) {
            personData.birthday = it
        }
        signUpViewModel.highSchool.observe(viewLifecycleOwner) {
            personData.highSchool = it
        }
        signUpViewModel.university.observe(viewLifecycleOwner) {
            personData.university = it
        }
        signUpViewModel.name.observe(viewLifecycleOwner) {
            personData.name = it
        }
        signUpViewModel.phone.observe(viewLifecycleOwner) {
            personData.phone = it
        }
        signUpViewModel.emailPasswordPair.observe(viewLifecycleOwner) {
            personData.emailPasswordPair = it
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.profile5_pick_image -> {
                val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
                intent.addCategory(Intent.CATEGORY_OPENABLE)
                intent.type = "*/*"
                pickFileLauncher.launch(intent)
            }
            R.id.profile5_back -> requireActivity().onBackPressed()
            R.id.profile5_male -> signUpViewModel.setGender(gender[0])
            R.id.profile5_female -> signUpViewModel.setGender(gender[1])
            R.id.profile5_black -> signUpViewModel.setHairColor(hairColor[0])
            R.id.profile5_brown -> signUpViewModel.setHairColor(hairColor[1])
            R.id.profile5_blond -> signUpViewModel.setHairColor(hairColor[2])
            R.id.profile5_red -> signUpViewModel.setHairColor(hairColor[3])
            R.id.profile5_grey -> signUpViewModel.setHairColor(hairColor[4])
            R.id.profile5_brunette -> signUpViewModel.setHairColor(hairColor[5])
            R.id.profile5_continue -> {
                hideKeyboard()
                if (personData.genderChoice == "") {
                    Snackbar.make(binding.root, getString(R.string.choose_your_gender), Snackbar.LENGTH_LONG).show()
                    return
                }
                if (personData.hairColorChoice == "") {
                    Snackbar.make(binding.root, getString(R.string.choose_your_hair_color), Snackbar.LENGTH_LONG).show()
                    return
                }
                if (personData.language.isEmpty()) {
                    Snackbar.make(binding.root, getString(R.string.choose_your_at_least_one_language), Snackbar.LENGTH_LONG).show()
                    return
                }
                if (byteIS == null) {
                    Snackbar.make(binding.root, getString(R.string.choose_your_profile_Image), Snackbar.LENGTH_LONG).show()
                    return
                }
                if (!isVerified) {
                    task = auth!!.reload()
                    task.addOnCompleteListener {
                        if (!auth!!.isEmailVerified) {
                            Snackbar.make(binding.root, getString(R.string.check_your_email_for_verification_link), Snackbar.LENGTH_LONG).show()
                        } else isVerified = true
                    }.addOnFailureListener {
                        Snackbar.make(binding.root, "${it.localizedMessage}", Snackbar.LENGTH_LONG).show()
                    }
                }

                if (!isVerified) return

                binding.profile5ContinueText.visibility = View.INVISIBLE
                binding.profile5Progress.visibility = View.VISIBLE
                ref = ref.child(getString(R.string.profile)).child(auth!!.uid)

                val strRef = FirebaseStorage.getInstance().reference
                    .child("profile_pics")
                    .child(FirebaseAuth.getInstance().currentUser!!.uid)

                val uploadTask = strRef.putBytes(byteIS!!)
                uploadTask.continueWith { task ->
                    if (!task.isSuccessful) task.exception?.let {
                        throw it
                    }
                    strRef.downloadUrl
                }.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        strRef.downloadUrl.addOnSuccessListener { downloadUri ->
                            personData.image = downloadUri.toString()
                            personData.auth = auth!!.uid
                            val timeMillis = Calendar.getInstance().timeInMillis
                            try {
                                personData.profileImages = arrayListOf(mapOf(timeMillis.toString() to personData.image))
                                ref.setValue(personData).addOnSuccessListener {
                                    binding.profile5ContinueText.visibility = View.VISIBLE
                                    binding.profile5Progress.visibility = View.INVISIBLE
                                    success = true
                                    requireActivity().supportFragmentManager.beginTransaction().addToBackStack("sign_up")
                                        .setCustomAnimations(R.anim.enter_right_to_left, R.anim.exit_right_to_left)
                                        .replace(R.id.profile5_root, FragmentSuccessReg())
                                        .commit()
                                }.addOnFailureListener {
                                    binding.profile5ContinueText.visibility = View.VISIBLE
                                    binding.profile5Progress.visibility = View.INVISIBLE
                                    Snackbar.make(binding.root, "${it.localizedMessage}", Snackbar.LENGTH_LONG).show()
                                }
                            } catch (e: Exception) {
                                Snackbar.make(binding.root, "${e.localizedMessage}", Snackbar.LENGTH_LONG).show()
                            }
                        }
                    }
                }.addOnFailureListener {
                    binding.profile5ContinueText.visibility = View.VISIBLE
                    binding.profile5Progress.visibility = View.INVISIBLE
                    Snackbar.make(binding.root, "${it.localizedMessage}", Snackbar.LENGTH_LONG).show()
                }
                scope.launch {
                    delay(10_000)
                    if (!success) {
                        if (!this@FragmentSignProfile5.isDetached)
                            requireActivity().runOnUiThread {
                                binding.profile5ContinueText.visibility = View.VISIBLE
                                binding.profile5Progress.visibility = View.INVISIBLE
                            }
                    }
                }
            }
        }
    }
}

class LanguagesAdapter : RecyclerView.Adapter<LanguagesAdapter.ViewHolder>() {
    var dataset: ArrayList<String> = arrayListOf()
    var selected: ArrayList<String> = arrayListOf()
    private lateinit var context: Context
    lateinit var signUpViewModel: SignUpViewModel
    lateinit var activity: Activity
    lateinit var viewLifecycleOwner: LifecycleOwner

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txt: TextView = itemView.findViewById(R.id.text_chip_text)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val view = LayoutInflater.from(context).inflate(R.layout.text_chip_row, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val datum = dataset[position]
        holder.txt.text = datum
        holder.itemView.setOnClickListener {
            try {
                val data = dataset[holder.adapterPosition]
                if (dataset[holder.adapterPosition] in selected) {
                    selected.remove(data)
                    holder.txt.setBackgroundResource(R.drawable.rounded_unselected)
                    signUpViewModel.setLanguage(selected)
                } else {
                    selected.add(data)
                    holder.txt.setBackgroundResource(R.drawable.rounded_selected)
                    signUpViewModel.setLanguage(selected)
                }
            } catch (e: Exception) {
            }
        }
    }

    override fun getItemCount() = dataset.size
}