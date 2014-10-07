TROPIUS
Territorial Remote Operated Pi Utility System
Created by: Bradley Johns

Introduction:
=============
TROPIUS is a Rasberry Pi operated home automation system
This README is for the Android Frontend, if you are looking for
a different (currently nonexistant) version of the front end or
for the Raspberry Pi backend look at the other TROPIUS repos


Installation:
=============
TROPIUS is lightyears from any form of respectable beta, and since
its a side project and not something I dedicate all of my time to, it
will probably remain that way for some time. That in mind, if you still
want to attempt running the TROPIUS android app, you should be able to
import this project into an eclipse or android studio environment, or simply
put the .apk file onto your device and try and run it like that.

Eventually if I manage to get a good enough version out I can try putting TROPIUS
on the public market so other people can play around with it. For now it'll stay
like this until I can make any real progress.


Navigation:
===========
The TROPIUS Android Frontend is divided into 3 main activites, (technically 4 but the
first one is invisible and should never actually show up). The sequence goes as follows:
    Config -- The activity that should run when TROPIUS first starts, assuming you have
              never configured it before. This activity stores network and user data
              for a specific TROPIUS device and stores it in a shared preferences file
              to be used by the other activities.
    
    Connect -- Hopefully you shouldn't spend much time in this activity. It attempts to
               establish a connection with a specified TROPIUS device and procedes to the
               control activity if it succeeds. Otherwise it will give you an error and
               a miscelanious pokemon quote and let you either try again or change your
               configuration settings.

    Control -- This is where to real work gets done. This activity is set up with different
               tabs for every device type that TROPIUS has. Each tab is populated with a
               series of commands and state notes about each given device on the selected
               table.


Developer Notes:
================
Oh hi there, I'm glad you decided to come down and read this part. I'm writing this note
during the initial commit because I'm stupid ambitious in the project, but I just want to
have this forward in here for future commits.

Tropius is designed to be not only easily implemented, but also easily modifiable. Making
a home automation system to be used in other people's setups is nice and all, but its a
little bit pointless if there are not extensions for what people are actually running.
For example, I don't happen to own an automated thermostat. That'd be a great thing to
implement, but right now I really have no purpose to work on it. That being said, if you
happen to setup a TROPIUS system, and you have enough development knowledge to make an
extension that works with your automated thermostat please go right ahead.

My only real request for third party (odd term for addons to my side project) addons is
that, if you manage to get them to work, please send them to me at BradJohns94@gmail.com
so I can add them to this git repository. I am really quite fond of the concept of TROPIUS
and anything I can add to make it more usable for other people would be fantastic. So if
you, for some reason, decide to implement my kind of janky automation system and feel like
making it better, please let me know. I'd like to thank you personally.

