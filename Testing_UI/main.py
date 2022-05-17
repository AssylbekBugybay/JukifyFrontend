from ssl import ALERT_DESCRIPTION_ACCESS_DENIED
from turtle import textinput
import cv2 as cv
import numpy as np
import os
import time
import pyautogui as gui
import mouse

#Global variables
delay = 1
tolerance = 0.8


def allActivitiesReachable():
    print('\n\tTest: allActivitiesReachable() is starting!\n') 
    """
    This activity tests if all activites reachable from the main activity are reachable.
    
    Requirements:
        a. The test starts from main activity
        b. There is already a spotify account logged in

    1. Check if main activity is open
    """
    checkMainActivity()
    
    """
    2. Click on "party history" button and check if the activity was opened
    """
    clickButton('btn_partyHistory.png')
    checkActivity('activity_partyHistory.png')
    
    clickBackButton()
    checkMainActivity()

    """
    3. Click on "create party" button and check if activity is open
    """
    print(checkPartyActivity('btn_createParty.png'))

    clickBackButton()
    checkMainActivity()

    """
    4. Click on "join party" button and check if activity is open
    """
    clickButton('btn_joinParty.png')
    checkActivity('activity_joinParty.png')

    clickBackButton()
    checkMainActivity()

    """
    5. Click on "settings" button and check if activity is open
    """
    clickButton('btn_settings.png')
    checkActivity('activity_settings.png')
    
    clickBackButton()
    checkMainActivity()

    """
    6. Print pass
    """
    print('\n\tTest: allActivitiesReachable() is finished!\n')

def functionalityLowerMenuButtons():
    print('\n\tTest: functionalityLowerMenuButtons() starting!\n')
    """
    This function tests that the buttons on the lower menu work correctly
    
    Requirements:
        a. The test starts from main activity
        b. There is already a spotify account logged in

    1. Check that we are in main activity
    """
    checkMainActivity()

    """
    2. Click on "join party" button
    """
    print(checkPartyActivity('btn_lower_createParty.png'))
    
    clickBackButton()
    checkMainActivity()

    """
    3. Click on "QR code" button
    """
    clickButton('btn_lower_QR.png')
    checkActivity('activity_QR.png')

    clickBackButton()
    checkMainActivity()

    """
    4. Click on "Settings" button
    """
    clickButton('btn_lower_settings.png')
    checkActivity('activity_settings.png')
    
    """
    5. From settings, click on button "Home" from lower menu
    """
    clickButton('btn_lower_home.png')
    checkMainActivity()

    """
    5. From settings, click on button "Home" from lower menu
    """
    clickButton('btn_share.png')
    checkItem('item_share.png')
    clickButton('btn_back_white.png')
    checkMainActivity()

    """
    6. Print that we are finished
    """
    print('\n\tTest: functionalityLowerMenuButtons() is finished!\n')

def createParty():
    print('\n\tTest: createParty() is starting!\n')
    """
    This function tests that a party gets created following the required steps

    Requirements: 
        a. The test starts from main activity
        b. There is already a spotify account logged in
        c. There needs to be an available player from the spotify user (open spotify on PC or personal phone)
    
    1. Check we are in main activity
    """
    checkMainActivity()

    """
    2. Click on create party button and check if the activity is open
    """
    print(checkPartyActivity('btn_createParty.png'))

    """
    3. Click on a playlist called "English" and check if it was selected
    """
    clickButton('btn_playlist.png')
    checkItem('item_startingPlaylist.png', 2)

    """
    4. Write a party name
    """
    inputText('input_partyName.png')
    write('UI Testing Party')
    
    """
    4. Click on create party and check if activity is open
    """
    print(clickCreateParty())

    """
    5. Print we finished
    """
    print('\n\tTest: createParty() is finished!\n')

def checkAddSongActivity():
    print('\n\tTest: checkAddSongActivity() is starting!\n')
    """
    This function tests if addSongActivity is reachable from party activity,
    and also if a song may be found.

    Requirements:
        a. The tesst starts from party activity

    1. Check that we are in the party activity
    """
    checkActivity('activity_party.png')

    """
    2. Clickk on add song button, check if addSongActivity was opened
    """
    clickButton('btn_addSong.png')
    checkActivity('activity_addSong.png')

    inputText('input_addSong.png')
    write('Despacito')

    checkItem('item_songToBeAdded.png')

    clickButton('btn_searchSong.png')

    clickButton('btn_songToBeAdded.png')

    clickButton('btn_hideKeyboard.png', 3)

    clickButton('btn_addSongToQueue.png')

    

    


    """
    n-1. Click on back button, and check if party activity is back
    """
    clickBackButton()

    checkActivity('activity_party.png')

    checkItem('btn_songToBeAdded.png')

    """
    n. Print we finished
    """
    print('\n\tTest: checkAddSongActivity() is finished!\n')

def prioritizeSong():
    print('\n\tTest: prioritizeSong() is starting!\n')
    """
    This function checks if inside a party, the priorization button works

    Requirements:
        a. The test starts from party activity

    1. Check that we are inside the party activity
    """
    checkActivity('activity_party.png')

    """
    2. Click on prioritize song button
    """
    clickButton('btn_prioritizeSong.png')

    """
    3. Check if song is now 2nd on the list
    """
    print(checkPrioritizedSong())

    """
    4. Print we finished
    """
    print('\n\tTest: prioritizeSong() is finished!\n')

def runAllTests():
    print('\n\tTest: runAllTests() is starting!\n')
    """
    This function runs all tests in a suitable order

    Requirements:
        a. The test starts from main activity
        b. There is already a spotify account logged in

    1. Run: allActivitiesReachable()
    """
    allActivitiesReachable()

    """
    2. Run: functionalityLowerMenuButtons()
    """
    functionalityLowerMenuButtons()

    """
    3. Run: createParty()
    """
    createParty()

    """
    4. Run: checkAddSongActivity()
    """
    checkAddSongActivity()

    """
    5. Run: prioritizeSong()
    """
    prioritizeSong()

    """
    6. Run: createPartyWithPartyName()
    """
    print('\nReturning to main...\n')
    print(goToMain())

    """
    7. Print we finished
    """
    print('\n\tTest: runAllTests() is finished!\n')
    

def checkPrioritizedSong():
    counter = 0
    tries = 10

    
    while(counter < tries):
        time.sleep(delay)
        if (gui.locateOnScreen('item_songPrioritized.png') == None):
            counter += 1
            clickButton('btn_prioritizeSong.png')
        else:
            return 'PASS\t\t Song was prioritized'
    return 'FAIL\t Song was not prioritized'       



def goToMain():
    counter = 0
    tries = 5

    while (counter < tries):
        time.sleep(1)
        if (gui.locateOnScreen('activity_main.png') == None):
            clickBackButton()
            counter += 1
        else:
            return 'PASS\t\t goToMain() reached main activity'
    return 'FAIL\t goToMain() took too many tries...'

def write(s):
    gui.write(s, 0.25)

def inputText(item):
    gui.click(item)

def clickCreateParty():
    tries = 5
    counter = 0
    
    while (counter < tries):
        clickButton('btn_createPartyActivity_createParty.png')
        time.sleep(10)
        if (gui.locateOnScreen('activity_party.png') == None):
            counter += 1
        else:
            return 'PASS\t\t clickCreateParty()'
        
    return 'FAIL\t clickCreateParty() failed...'


def checkPartyActivity(btn):
    #This function is because of a small bug related with the emulator where it throws a network error
    tries = 5
    counter = 0
    #Open create party activity
    clickButton(btn)

    #Try n number of times
    while (counter < tries):
        time.sleep(5)
        #Normal createParty activity was not found
        if (gui.locateOnScreen('activity_createParty.png') == None):
            #The createParty activity with the bug has been found
            if (gui.locateOnScreen('activity_createParty_empty.png') != None):
                clickBackButton()
                time.sleep(5)
                clickButton(btn)
                counter += 1
            #The createParty activity with the bug was not found
            else:
                return 'FAIL\t checkPartyActivity() failed...Since not emptyCreateParty activity was found' 
        #Normal createParty activity was found
        else:
            return 'PASS\t\t checkPartyActivity(' + btn + ')'

    return 'FAIL\t checkPartyActivity(' + btn + ') failed...After trying' + str(tries) + ' times'

def clickButton(btn, delay=delay, input=False):
    """
    This function clicks a button and throws an exception if button wasn't found
    
    :param btn: name of the image of the button to be clicked
    """

    try:
        time.sleep(delay)
        gui.click(btn)
        print('PASS\t\t Clicked on: ' + btn)
    except:
        print('FAIL\t clickButton(' + btn + ') failed...')

def checkActivity(activity, delay=delay):
    """ 
    This function tests if 'activity' is open
    
    :param activity: name of the image of the activity to be checked
    """
    time.sleep(delay)
    if (gui.locateOnScreen(activity, confidence=tolerance) != None):
        print('PASS\t\t Found ' + activity)
    else:
        print('FAIL\t checkActivity(' + activity + ') failed...')

def checkItem(item, delay=delay):
    """
    This function tests if 'item' is on screen
    
    :param item: the item we are looking for
    """
    
    time.sleep(delay)
    if (gui.locateOnScreen(item, confidence=tolerance) != None):
        print('PASS \t\t Found ' + item)
    else:
        print('FAIL\t checkItem(' + item + ') failed...')

def checkMainActivity():
    checkActivity('activity_main.png')

def clickBackButton(delay=delay):
    clickButton('btn_back.png', delay)

def printMenu():
    print()
    print('1 - Check if all activities are reachable')
    print('2 - Check functionality of lower menu buttons')
    print('3 - Create a party')
    print('4 - Add song activity (start from party activity)')
    print('5 - Prioritize a song (start from party activity)')
    print('6 - Run all tests in a suitable order')
    print('7 - Print options again')
    print('8 - Exit...')
    print()

def main():
    while(True):
        printMenu()
        option = ''
        try:
            option = int(input('Select a test to be run: '))
        except:
            print('Wrong input. Please enter a number...')

        if option == 1:
            allActivitiesReachable()
        elif option == 2:
            functionalityLowerMenuButtons()
        elif option == 3:
            createParty()
        elif option == 4:
            checkAddSongActivity()
        elif option == 5:
            prioritizeSong()
        elif option == 6:
            runAllTests()
        elif option == 7:
            pass
        elif option == 8:
            print('Ciao bacalao...')
            exit()
        else:
            print('Wrong input! Please input a number from 1 to 6')

if __name__ == '__main__':
    main()
