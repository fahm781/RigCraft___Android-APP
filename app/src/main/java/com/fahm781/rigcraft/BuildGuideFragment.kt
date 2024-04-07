package com.fahm781.rigcraft

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
private val steps = listOf(
    InstructionStep(
        "Install the CPU",
        "1. Locate the CPU socket on your motherboard. \n2. lift the socket handle to insert the CPU ensuring the " +
                "pins are aligned. \n3. Lower the socket handle to secure the CPU in place.",
        R.drawable.insert_cpu
    ),
    InstructionStep(
        "Install the RAM",
        "1. Identify the RAM slots on your motherboard.\n2. If you have a lesser number of Ram than the number of slots on the motherboard " +
                "refer to your motherboard manual. \n3. Insert the RAM sticks into the correct slots by applying even pressure.",
        R.drawable.insert_ram
    ),
    InstructionStep(
        "Applying the Thermal Paste",
        "1. Clean CPU surface with isopropyl alcohol. \n2. Place a pea-sized dot of thermal paste on CPU center. \n3. Position heatsink on top and press down evenly. \n4. Secure heatsink with provided mechanism. \n5. Power on to ensure correct application."
    , R.drawable.apply_thermal_paste),
    InstructionStep(
        "Installing CPU Fan",
        "1. Align the CPU fan over the motherboard's CPU socket. \n2. Clip or screw the fan onto the heatsink if separate. \n3. Plug the fan's power connector into the motherboard CPU fan header. \n4. Ensure the fan is securely fastened and wires are not obstructing any components."
    , R.drawable.instal_cpu_cooler),
    InstructionStep(
        "Installing Motherboard into the PC Case",
        "1. Prepare the case by installing standoffs where the motherboard will sit. \n2. Align the motherboard with the I/O shield and case standoffs. \n3. Secure the motherboard to the standoffs with screws. \n4. Ensure no loose screws or objects are behind the motherboard. \n5. Verify all ports are accessible through the I/O shield."
 , R.drawable.install_mobo_in_case),
    InstructionStep(
        "Connecting Front I/O and other connectors to the Motherboard",
        "1. Connect the Front I/O to the motherboard. This includes audio cables, front USB 3.0 header and the power button cables (refer to motherboard user manual). \n2. Plug in the any pc fans to the relevant headers."
 , R.drawable.connect_front_io),
    InstructionStep(
        "Installing and connecting Hard Drive/SSD/NVME",
    "Note: For Hard Drive/SSD follow step 1,4 and 5. For Nvme's follow step 2 and 3.\n\n 1. Mount SATA drive in case bay and secure with screws. \n2. Insert NVMe SSD into M.2 slot at a slight angle. \n3. Push down NVMe gently and fasten with the standoff screw. \n4. Connect SATA data cables to motherboard and drives. \n5. Connect power cables from PSU to SATA drives."
 , R.drawable.installing_storage),
    InstructionStep(
        "Install and connect Power Supply",
    "1. Place the power supply unit (PSU) into its bay in the case. \n2. Secure the PSU with screws to the case chassis. \n3. Connect the motherboard power cables (24-pin and 8-pin CPU power). \n4. Connect SATA or Molex power connectors to drives and peripherals. \n5. Manage cables for unobstructed airflow and neatness."
 , R.drawable.install_power_supply,),
    InstructionStep(
        "Install and connect the Graphics Card",
    "1. Remove the expansion slot cover on the case. \n2. Align the graphics card with the PCI-E slot on the motherboard. \n3. Push down the graphics card until it clicks into place. \n4. Secure the graphics card to the case with screws. \n5. Connect the graphics card to the PSU with the relevant power cables. 6. Thats It! Now hit the power button and hope for the best!"
    ,  R.drawable.installing_gpu
    )
    )
/**
 * A simple [Fragment] subclass.
 * Use the [BuildGuideFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class BuildGuideFragment : Fragment() {

    private lateinit var buttonNext: Button
    private lateinit var buttonPrevious: Button
    private lateinit var textViewTitle: TextView
    private lateinit var textViewContent: TextView
    private lateinit var instructionImageView: ImageView
    private lateinit var stepsSpinner: Spinner
    private var currentStepIndex = 0
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_build_guide, container, false)

        buttonNext = view.findViewById(R.id.buttonNext)
        buttonPrevious = view.findViewById(R.id.buttonPrevious)
        textViewTitle = view.findViewById(R.id.textViewTitle)
        textViewContent = view.findViewById(R.id.textViewContent)
        instructionImageView = view.findViewById(R.id.instructionImageView)

        updateUI()
        buttonNext.setOnClickListener {
            moveToNextStep()
            updateUI()
        }

        buttonPrevious.setOnClickListener {
            moveToPreviousStep()
            updateUI()
        }

        stepsSpinner = view.findViewById(R.id.stepsSpinner)
        ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, steps.map { it.title }).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            stepsSpinner.adapter = adapter
        }

        stepsSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                currentStepIndex = position
                updateUI()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        }

        return view;
    }

    private fun getCurrentStep(): InstructionStep {
        if (currentStepIndex >= 0 && currentStepIndex < steps.size) {
            return steps[currentStepIndex]
        }
        // Handle index out of bounds or other error case
        return InstructionStep("Invalid Heading Index", "Invalid step index", 0)
    }

    private fun moveToNextStep() {
        if (currentStepIndex < steps.size - 1) {
            currentStepIndex++
        }
    }

    private fun moveToPreviousStep() {
        if (currentStepIndex > 0) {
            currentStepIndex--
        }
    }

    private fun updateUI() {
        val currentStep = getCurrentStep()
        textViewTitle.text = currentStep.title
        textViewContent.text = currentStep.content
        instructionImageView.setImageResource(currentStep.imageResId)
        // Disable the previous button if the current step is the first step
        buttonPrevious.isEnabled = currentStepIndex != 0
        // Disable the next button if the current step is the last step
        buttonNext.isEnabled = currentStepIndex != steps.size - 1
    }
}


