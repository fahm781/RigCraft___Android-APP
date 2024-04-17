package com.fahm781.rigcraft.buildGuidePackage
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.fahm781.rigcraft.R


class BuildGuideViewModel : ViewModel() {

    val steps = listOf(
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
            "1. Clean CPU surface with isopropyl alcohol. \n2. Place a pea-sized dot of thermal paste on CPU center. \n3. Position heatsink on top and press down evenly. \n4. Secure heatsink with provided mechanism. \n5. Power on to ensure correct application.",
            R.drawable.apply_thermal_paste
        ),
        InstructionStep(
            "Installing CPU Fan",
            "1. Align the CPU fan over the motherboard's CPU socket. \n2. Clip or screw the fan onto the heatsink if separate. \n3. Plug the fan's power connector into the motherboard CPU fan header. \n4. Ensure the fan is securely fastened and wires are not obstructing any components.",
            R.drawable.instal_cpu_cooler
        ),
        InstructionStep(
            "Installing Motherboard into the PC Case",
            "1. Prepare the case by installing standoffs where the motherboard will sit. \n2. Align the motherboard with the I/O shield and case standoffs. \n3. Secure the motherboard to the standoffs with screws. \n4. Ensure no loose screws or objects are behind the motherboard. \n5. Verify all ports are accessible through the I/O shield.",
            R.drawable.install_mobo_in_case
        ),
        InstructionStep(
            "Connecting Front I/O and other connectors to the Motherboard",
            "1. Connect the Front I/O to the motherboard. This includes audio cables, front USB 3.0 header and the power button cables (refer to motherboard user manual). \n2. Plug in the any pc fans to the relevant headers.",
            R.drawable.connect_front_io
        ),
        InstructionStep(
            "Installing and connecting Hard Drive/SSD/NVME",
            "Note: For Hard Drive/SSD follow step 1,4 and 5. For Nvme's follow step 2 and 3.\n\n 1. Mount SATA drive in case bay and secure with screws. \n2. Insert NVMe SSD into M.2 slot at a slight angle. \n3. Push down NVMe gently and fasten with the standoff screw. \n4. Connect SATA data cables to motherboard and drives. \n5. Connect power cables from PSU to SATA drives.",
            R.drawable.installing_storage
        ),
        InstructionStep(
            "Install and connect Power Supply",
            "1. Place the power supply unit (PSU) into its bay in the case. \n2. Secure the PSU with screws to the case chassis. \n3. Connect the motherboard power cables (24-pin and 8-pin CPU power). \n4. Connect SATA or Molex power connectors to drives and peripherals. \n5. Manage cables for unobstructed airflow and neatness.",
            R.drawable.install_power_supply,
        ),
        InstructionStep(
            "Install and connect the Graphics Card",
            "1. Remove the expansion slot cover on the case. \n2. Align the graphics card with the PCI-E slot on the motherboard. \n3. Push down the graphics card until it clicks into place. \n4. Secure the graphics card to the case with screws. \n5. Connect the graphics card to the PSU with the relevant power cables. 6. Thats It! Now hit the power button and hope for the best!",
            R.drawable.installing_gpu
        )
    )

    private var _currentStepIndex = MutableLiveData(0)
    val currentStepIndex: LiveData<Int> = _currentStepIndex

    private val _currentStep = MutableLiveData(steps[_currentStepIndex.value!!])
    val currentStep: LiveData<InstructionStep> = _currentStep

    fun moveToNextStep() {
        if (_currentStepIndex.value!! < steps.size - 1) {
            _currentStepIndex.value = _currentStepIndex.value!! + 1
            _currentStep.value = steps[_currentStepIndex.value!!]
        }
    }
    fun updateCurrentStepIndex(index: Int) {
        _currentStepIndex.value = index
        _currentStep.value = steps[index]
    }

    fun moveToPreviousStep() {
        if (_currentStepIndex.value!! > 0) {
            _currentStepIndex.value = _currentStepIndex.value!! - 1
            _currentStep.value = steps[_currentStepIndex.value!!]
        }
    }
}