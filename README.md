# PandaSDK

[![](https://jitpack.io/v/AppSci/panda-sdk-android.svg)](https://jitpack.io/#AppSci/panda-sdk-android)


    implementation 'com.github.AppSci:panda-sdk-android:$latestVersion'



Panda SDK is a lightweight open-source Kotlin library to easily integrate purchase screens into your app without coding.

Visit our website for details: https://app.panda.boosters.company/

## Features

👍 Integrating subscriptions using our SDK is very easy.<br/>Panda takes care of a subscription purchase flow. Integrate SDK in just a few lines of code.

🎨 Create subscription purchase screens without coding - just use html.<br/>You don't need to develop purchase screens. So easy!


## Initialize Panda SDK

To set up Panda SDK you will need API Key. [Register](https://app.panda.boosters.company/) your app in Panda Web and get your API key.

```
class App : Application() {
    override fun onCreate() {
        super.onCreate()
        Panda.configure(application, BuildConfig.PANDA_API_KEY, BuildConfig.DEBUG)
    }
}

```

#### Working with Screens from Panda
For getting screen from Panda Web you should use 

```
func getScreen(type: ScreenType?, id: String?, onSuccess: ((Fragment) -> Unit)?, onError: ((Throwable) -> Unit)?)
```

It returns Fragment with you subscription screen. Just add this fragment to you activity or another Fragment.

If you wanna prefetch screen to ensure that it will be ready before displaying it, you can use 

```
func prefetchScreen(type: ScreenType?, id: String?, onComplete: (() -> Unit)?, onError: ((Throwable) -> Unit)?)
```

We recommend you prefetch screen right after Panda SDK is configured:

```
override fun onCreate() {
    Panda.configureRx(application, BuildConfig.PANDA_API_KEY, BuildConfig.DEBUG)
                .flatMapCompletable { Panda.prefetchSubscriptionScreenRx() }
                .subscribe(DefaultCompletableObserver())
}
```

PandaSDK uses Default screen in case of any errors - e.g. no inet connection - you should add "panda-index.html" screen in assets folder - name is critical  - it should be named exactly "panda-index.html" - we will use it for displaying this screen in case of any errors


## Resources

To have all set, you need to add this info in your strings resources file - you can create it by your own or download  `panda-strings.xml` from Example - resourcess are mandatory and names are critical

```
<resources>
    <string-array name="panda_subscriptions">
        <item>your_subscription_1</item>
        <item>your_subscription_2</item>
    </string-array>
    <string-array name="panda_products">
        <item>your_product_1</item>
        <item>your_product_2</item>
    </string-array>
    <string name="panda_policy_url">https://policy.com/</string>
    <string name="panda_terms_url">https://terms.html</string>
</resources>
```

## Handle Subscriptions

Panda SDK provides a set of methods to manage subscriptions. 

### Fetch Products

Panda SDK automatically fetches google purchases upon launch and verifies them on server. You can call 'sync' function if user made purchase out of Panda SDK: 
```
Panda.syncSubscriptions()
```
### Make a Purchase

To make a purchase - you are creating html with products_ids for Purchases - Panda SDK upon clicking on concreate button or view gets this product_id for purchase & you just need to implement callbacks for successful purchase or error :

```
private val listener = object : PandaListener {
        override fun onDismissClick() {
        }

        override fun onError(t: Throwable) {
        }

        override fun onPurchase(id: String) {  
        }

        override fun onRestore(ids: List<String>) {
        }
    }
    
    override fun onStart() { 
        super.onStart()
        Panda.addListener(listener)
    }

    override fun onStop() {
        Panda.removeListener(listener)
        super.onStop()
    }
```

### Restore Purchases

 Restore Purchase is called when user tap on `Restore purchase` button on html screen. You can handle this restore by implementing callback
 ```
 fun onRestore(ids: List<String>)
 ```

Basically it just fetches google purchases and sends to backend.

### Skipping Purchase Process
You can allow your users to skip all purchase process - when user tap cross on Screen, you can allow user to go futher into your app - this callback is called 

```
fun onDismissClick()
```

## Having troubles?

If you have any questions or troubles with SDK integration feel free to contact us. We are online.

*Like Panda? Place a star at the top 😊*

