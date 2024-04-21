package com.fahm781.rigcraft
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import com.fahm781.rigcraft.buildGuidePackage.BuildGuideViewModel


class BuildGuideFragment : Fragment() {

    private val viewModel: BuildGuideViewModel by viewModels()
    private lateinit var buttonNext: Button
    private lateinit var buttonPrevious: Button
    private lateinit var textViewTitle: TextView
    private lateinit var textViewContent: TextView
    private lateinit var instructionImageView: ImageView
    private lateinit var stepsSpinner: Spinner

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =  inflater.inflate(R.layout.fragment_build_guide, container, false)

        buttonNext = view.findViewById(R.id.buttonNext)
        buttonPrevious = view.findViewById(R.id.buttonPrevious)
        textViewTitle = view.findViewById(R.id.textViewTitle)
        textViewContent = view.findViewById(R.id.textViewContent)
        instructionImageView = view.findViewById(R.id.instructionImageView)
        stepsSpinner = view.findViewById(R.id.stepsSpinner)

        // Observe the current step and update the UI
        viewModel.currentStep.observe(viewLifecycleOwner) { step ->
            textViewTitle.text = step.title
            textViewContent.text = step.content
            instructionImageView.setImageResource(step.imageResId)
        }
        // Observe the current step index and update the UI
        viewModel.currentStepIndex.observe(viewLifecycleOwner) { index ->
            stepsSpinner.setSelection(index)
            buttonPrevious.isEnabled = index != 0
            buttonNext.isEnabled = index != viewModel.steps.size - 1
        }
        //pressing next button will move to the next step
        buttonNext.setOnClickListener {
            viewModel.moveToNextStep()
        }

        //pressing previous button will move to the previous step
        buttonPrevious.setOnClickListener {
            viewModel.moveToPreviousStep()
        }

        //setting up the spinner with the steps
        ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, viewModel.steps.map { it.title }).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            stepsSpinner.adapter = adapter
        }

        //selecting a step from the spinner will update the current step index
        stepsSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                viewModel.updateCurrentStepIndex(position)
            }
            override fun onNothingSelected(parent: AdapterView<*>) {
            }
        }
        return view
    }
}


