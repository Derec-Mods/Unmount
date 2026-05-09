## Fix Minecarts cluttering your survival server rails!

_This was created for use on my forever world, called "derex smp". Check it out in action, server ip: `mc.longhorns.dev`_

Vanilla minecart physics can be notoriously clunky. This is a lightweight plugin that overhauls minecart interactions by completely preventing the annoying "bounce-back" effect when two minecarts collide. 

Additionally, it includes a seamless "Autobreak on Dismount" feature automatically breaking minecarts and dropping them as items the exact moment a player leaves them. Perfect for keeping your railways clean and clutter-free!

Designed to be simple, server-friendly, and as vanilla as possible.


```
commands:
  togglecart:
    description: Toggle minecart auto-break on dismount for yourself.
    usage: /togglecart
    aliases: [autobreak]
    permission: unmount.togglecart
    permission-message: You don't have permission to use this command.

permissions:
  unmount.togglecart:
    description: Allows using /togglecart (and /autobreak)
    default: true

```


License: MIT

## Join our dev discord!  
![Discord](https://i.imgur.com/u3V5mdF.png)
[https://discord.gg/HM5XEe6pW6](https://discord.gg/HM5XEe6pW6)

## ❤️ Credits:

Developed by DerexXD

Image inspired from Xannosz's Better Minecarts
