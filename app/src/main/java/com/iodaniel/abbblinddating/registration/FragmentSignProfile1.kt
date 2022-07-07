package com.iodaniel.abbblinddating.registration

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.snackbar.Snackbar
import com.iodaniel.abbblinddating.R
import com.iodaniel.abbblinddating.databinding.FragmentSignProfile1Binding
import com.iodaniel.abbblinddating.util.Keyboard.hideKeyboard
import com.iodaniel.abbblinddating.viewModel.SignUpViewModel
import java.util.*

class FragmentSignProfile1 : Fragment(), View.OnClickListener {
    private lateinit var binding: FragmentSignProfile1Binding
    private val signUpViewModel: SignUpViewModel by activityViewModels()
    private val selectionAdapter = SelectionAdapter()
    private var interests: ArrayList<String> = arrayListOf()
    private var listMonths: ArrayList<String> = arrayListOf()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentSignProfile1Binding.inflate(inflater, container, false)
        listMonths = arrayListOf(getString(R.string.january), getString(R.string.february), getString(R.string.march), getString(R.string.april),
            getString(R.string.may), getString(R.string.june), getString(R.string.july), getString(R.string.august),
            getString(R.string.september), getString(R.string.october), getString(R.string.november), getString(R.string.december))
        binding.profile1Back.setOnClickListener(this)
        binding.profile1Age.setOnClickListener(this)
        binding.profile1Continue.setOnClickListener(this)
        signUpViewModel.birthday.observe(viewLifecycleOwner) {
            binding.profile1Day.text = it["day"].toString()
            binding.profile1Month.text = listMonths[it["month"]!!]
            binding.profile1Year.text = it["year"].toString()
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        signUpViewModel.interests.observe(viewLifecycleOwner) {
            interests = it
        }
        selectionAdapter.signUpViewModel = signUpViewModel
        selectionAdapter.dataset = arrayListOf(getString(R.string.books), getString(R.string.sports), getString(R.string.music),
            getString(R.string.cooking), getString(R.string.dance), getString(R.string.design),
            getString(R.string.technology), getString(R.string.athlete), getString(R.string.gambling))
        selectionAdapter.selected = interests
        selectionAdapter.activity = requireActivity()
        binding.rvInterests.adapter = selectionAdapter
        binding.rvInterests.layoutManager = StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.HORIZONTAL)
    }

    //override fun onResume() {
    //    super.onResume()
    //    val  window = requireActivity().window;
    //    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
    //    window.statusBarColor = Color.parseColor("#EDFDFF")
    //}

    private fun datePicker() {
        val dateRangePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText(getString(R.string.birthday))
            .build()
        dateRangePicker.show((activity as AppCompatActivity).supportFragmentManager, "date")
        dateRangePicker.addOnPositiveButtonClickListener {
            val calendarS = Calendar.getInstance()
            calendarS.timeInMillis = it
            val day = calendarS.get(Calendar.DAY_OF_MONTH)
            val month = calendarS.get(Calendar.MONDAY)
            val year = calendarS.get(Calendar.YEAR)
            signUpViewModel.setBirthday(mapOf("day" to day, "month" to month, "year" to year))
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.profile1_back -> requireActivity().onBackPressed()
            R.id.profile1_age -> datePicker()
            R.id.profile1_continue -> {
                hideKeyboard()
                if (binding.profile1Day.text.trim().toString() == "Day") {
                    Snackbar.make(binding.root, getString(R.string.select_your_birthday), Snackbar.LENGTH_LONG).show()
                    return
                }
                if (interests.isEmpty()) {
                    Snackbar.make(binding.root, getString(R.string.select_at_least_one_interest), Snackbar.LENGTH_LONG).show()
                    return
                }
                requireActivity().supportFragmentManager.beginTransaction().addToBackStack("sign_up")
                    .setCustomAnimations(R.anim.enter_right_to_left, R.anim.exit_right_to_left)
                    .replace(R.id.profile1_root, FragmentSignProfile2())
                    .commit()
            }
        }
    }
}

class SelectionAdapter : RecyclerView.Adapter<SelectionAdapter.ViewHolder>() {
    var dataset: ArrayList<String> = arrayListOf()
    var selected: ArrayList<String> = arrayListOf()
    private lateinit var context: Context
    lateinit var signUpViewModel: SignUpViewModel
    lateinit var activity: Activity
    lateinit var viewLifecycleOwner: LifecycleOwner

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txt: TextView = itemView.findViewById(R.id.text_chip_text)
        //val card: CardView = itemView.findViewById(R.id.text_chip_card)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val view = LayoutInflater.from(context).inflate(R.layout.text_chip_row, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val datum = dataset[position]
        holder.txt.text = datum
        signUpViewModel.setInterests(selected)
        holder.itemView.setOnClickListener {
            try {
                val data = dataset[holder.adapterPosition]
                if (dataset[holder.adapterPosition] in selected) {
                    selected.remove(data)
                    holder.txt.setBackgroundResource(R.drawable.rounded_unselected)
                    signUpViewModel.setInterests(selected)
                } else {
                    selected.add(data)
                    holder.txt.setBackgroundResource(R.drawable.rounded_selected)
                    signUpViewModel.setInterests(selected)
                }
            } catch (e: Exception) {
            }
        }
    }

    override fun getItemCount() = dataset.size
}