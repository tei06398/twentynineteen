## Welcome!
This GitHub repository contains the source code that is used to build the Android app used to control the RoboWarriors' *FIRST* Tech Challenge competition robot.  To use this SDK, follow the instructions below.

##6460 Programming Handbook
September 24th, 2018

This year, we would like to try to work more closely with Git, for version control, and code review, to make sure any changes that are pushed are meant to be there, and are functional.
In implementing these changes, we would like to impose a set of rules and guidelines that should not under most any circumstances be broken. These are as follows:

NEVER commit straight to master - instead, open a pull request under a new branch and submit that for review and eventually, a merge.


Put effort into your pull request message. Detail the changes made, so if/when we are looking for a commit that caused a specific problem, we can narrow it down to commits that deal with certain functionality.


Unless having confirmed with the rest of the programming team that it is safe to edit files outside of your sub-subteam (Auton or TeleOp), DO NOT edit said files.


Unless a majority of the programming team agrees that a force reset to the head branch is necessary, DON’T DO IT. We’ve had to in previous years, but hopefully with further review, we won’t have to this year.


In any situation, think logically about what you’re doing. Being programmers, this seems like it should come naturally to us, but sometimes you just get lost in what you’re doing so much that you don’t stop to think about why you’re doing it.

We’ll be trying to keep core functionality to a limit of 3 files this year, to be expanded if necessary. These files are planned to be as follows:


DriverFunction
GunnerFunction
SensorFunction / AutonFunction

These shouldn’t rely on any other class built for functionality and should be self-contained.

## Downloading the Project
It is important to note that this repository is large and can take a long time and use a lot of space to download.

### On a Personal (Windows) Computer
Install JDK 8

Install Git for Windows

Install IntelliJ

Clone https://github.com/tei06398/twentynineteen.git

When prompted to import from build.common.gradle, click "Yes"

After Project Loads, Ctrl+Alt+S

Appearance & Behavior > System Settings > Android SDK

Where it says "Android SDK Location", there is a hyperlink that says "Edit" - Click it, wait for download

A window will appear, titled "SDK Setup" - Leave all of the items checked.

Where it says Android SDK Location, set it to "C:\Android\SDK" to prevent problems with white space

Press Next Twice

Wait for Install

When the output log displays "Android SDK is up to date.", click "Finish".

Close the Settings Window

File > Close Project

Open "twentynineteen" from the quick select menu

The output log will display "Failed to find target with hash string 'android-23' in: C:\Android\SDK"

Click the hyperlink to "Install missing platform(s) and sync project"

Accept the License Agreement

Press Next

Wait for Install

When the progress bar is labelled "Done", press "Finish"

The output log will display "Failed to find Build Tools revision 27.0.3"

Click the hyperlink to "Install Build Tools 27.0.3 and sync project"

Wait for Install

When the progress bar is labelled "Done", press "Finish"

**Your install should now be Complete!**

**************************************************************************************
# Version Info
**************************************************************************************

Version 4.0 (released on 09/12/2018)

Changes include:
 * Initial support for UVC compatible cameras 
    - If UVC camera has a unique serial number, RC will detect and enumerate by serial number.
    - If UVC camera lacks a unique serial number, RC will only support one camera of that type connected.
    - Calibration settings for a few cameras are included (see TeamCode/src/main/res/xml/teamwebcamcalibrations.xml for details).
    - User can upload calibration files from Program and Manage web interface.
    - UVC cameras seem to draw a fair amount of electrical current from the USB bus.
         + This does not appear to present any problems for the REV Robotics Control Hub.
	 + This does seem to create stability problems when using some cameras with an Android phone-based Robot Controller.
	 + FTC Tech Team is investigating options to mitigate this issue with the phone-based Robot Controllers.
    - Updated sample Vuforia Navigation and VuMark Op Modes to demonstrate how to use an internal phone-based camera and an external UVC webcam.    

 * Support for improved motor control.
    - REV Robotics Expansion Hub firmware 1.8 and greater will support a feed forward mechanism for closed loop motor control.
    - FTC SDK has been modified to support PIDF coefficients (proportional, integral, derivative, and feed forward).
    - FTC Blocks development tool modified to include PIDF programming blocks.
    - Deprecated older PID-related methods and variables.
    - REV's 1.8.x PIDF-related changes provide a more linear and accurate way to control a motor.

 * Wireless
    - Added 5GHz support for wireless channel changing for those devices that support it.
        + Tested with Moto G5 and E4 phones.
	+ Also tested with other (currently non-approved) phones such as Samsung Galaxy S8.

* Improved Expansion Hub firmware update support in Robot Controller app
    - Changes to make the system more robust during the firmware update process (when performed through Robot Controller app).
    - User no longer has to disconnect a downstream daisy-chained Expansion Hub when updating an Expansion Hub's firmware.
        + If user is updating an Expansion Hub's firmware through a USB connection, he/she does not have to disconnect RS485 connection to other Expansion Hubs.
	+ The user still must use a USB connection to update an Expansion Hub's firmware.
	+ The user cannot update the Expansion Hub firmware for a downstream device that is daisy chained through an RS485 connection.
    - If an Expansion Hub accidentally gets "bricked" the Robot Controller app is now more likely to recognize the Hub when it scans the USB bus.
        + Robot Controller app should be able to detect an Expansion Hub, even if it accidentally was bricked in a previous update attempt.
	+ Robot Controller app should be able to install the firmware onto the Hub, even if if accidentally was bricked in a previous update attempt.
 
 * Resiliency
    - FTC software can detect and enable an FTDI reset feature that is available with REV Robotics v1.8 Expansion Hub firmware and greater.
        + When enabled, the Expansion Hub can detect if it hasn't communicated with the Robot Controller over the FTDI (USB) connection.
	+ If the Hub hasn't heard from the Robot Controller in a while, it will reset the FTDI connection.
	+ This action helps system recover from some ESD-induced disruptions.
    - Various fixes to improve reliability of FTC software.
     
 * Blocks
    - Fixed errors with string and list indices in blocks export to java.
    - Support for USB connected UVC webcams.
    - Refactored optimized Blocks Vuforia code to support Rover Ruckus image targets.
    - Added programming blocks to support PIDF (proportional, integral, derivative and feed forward) motor control.
    - Added formatting options (under Telemetry and Miscellaneous categories) so user can set how many decimal places to display a numerical value.
    - Support to play audio files (which are uploaded through Blocks web interface) on Driver Station in addition to the Robot Controller.
    - Fixed bug with Download Image of Blocks feature.
    - Support for REV Robotics Blinkin LED Controller.
    - Support for REV Robotics 2m Distance Sensor.
    - Added support for a REV Touch Sensor (no longer have to configure as a generic digital device).
    - Added blocks for DcMotorEx methods.
        + These are enhanced methods that you can use when supported by the motor controller hardware.
	+ The REV Robotics Expansion Hub supports these enhanced methods.
	+ Enhanced methods include methods to get/set motor velocity (in encoder pulses per second), get/set PIDF coefficients, etc..

 * Modest Improvements in Logging
    - Decrease frequency of battery checker voltage statements.
    - Removed non-FTC related log statements (wherever possible).
    - Introduced a "Match Logging" feature.
        + Under "Settings" a user can enable/disable this feature (it's disabled by default).
	+ If enabled, user provides a "Match Number" through the Driver Station user interface (top of the screen).
	    * The Match Number is used to create a log file specifically with log statements from that particular Op Mode run.
	    * Match log files are stored in /sdcard/FIRST/matlogs on the Robot Controller.
	    * Once an op mode run is complete, the Match Number is cleared.
	    * This is a convenient way to create a separate match log with statements only related to a specific op mode run.
 
 * New Devices
    - Support for REV Robotics Blinkin LED Controller.
    - Support for REV Robotics 2m Distance Sensor.
    - Added configuration option for REV 20:1 HD Hex Motor.
    - Added support for a REV Touch Sensor (no longer have to configure as a generic digital device).
    
 * Miscellaneous
    - Fixed some errors in the definitions for acceleration and velocity in our javadoc documentation.
    - Added ability to play audio files on Driver Station
    - When user is configuring an Expansion Hub, the LED on the Expansion Hub will change blink pattern (purple-cyan)  to indicate which Hub is currently being configured.
    - Renamed I2cSensorType to I2cDeviceType.
    - Added an external sample Op Mode that demonstrates localization using 2018-2019 (Rover Ruckus presented by QualComm) Vuforia targets.
    - Added an external sample Op Mode that demonstrates how to use the REV Robotics 2m Laser Distance Sensor.
    - Added an external sample Op Mode that demonstrates how to use the REV Robotics Blinkin LED Controller.
    - Re-categorized external Java sample Op Modes to "TeleOp" instead of "Autonomous".
    
Known issues:
 * Initial support for UVC compatible cameras
    - UVC cameras seem to draw significant amount of current from the USB bus.
        + This does not appear to present any problems for the REV Robotics Control Hub.
	+ This does seem to create stability problems when using some cameras with an Android phone-based Robot Controller.
	+ FTC Tech Team is investigating options to mitigate this issue with the phone-based Robot Controllers.
    - There might be a possible deadlock which causes the RC to become unresponsive when using a UVC webcam with a Nougat Android Robot Controller.

 * Wireless
    - When user selects a wireless channel, this channel does not necessarily persist if the phone is power cycled.
        + Tech Team is hoping to eventually address this issue in a future release.
	+ Issue has been present since apps were introduced (i.e., it is not new with the v4.0 release).
    - Wireless channel is not currently displayed for WiFi Direct connections.

 * Miscellaneous
    - The blink indication feature that shows which Expansion Hub is currently being configured does not work for a newly created configuration file.
        + User has to first save a newly created configuration file and then close and re-edit the file in order for blink indicator to work.


**************************************************************************************
# Old Release Information
**************************************************************************************
Older Version Info can be found at https://github.com/ftctechnh/ftc_app/
