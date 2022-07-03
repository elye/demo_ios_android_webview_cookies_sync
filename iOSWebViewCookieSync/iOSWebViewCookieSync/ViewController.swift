import UIKit
import WebKit
import SafariServices

extension URL {
    static var localWebCookieSetter: URL {
        return URL(string: "http://127.0.0.1/webview-cookie-set.php")!
    }
    static var localWebServer: URL {
        return URL(string: "http://127.0.0.1/webview-cookie-read.php")!
    }
    static var localApiServer: URL {
        return URL(string: "http://127.0.0.1/api-cookie-set.php")!
    }
}

class ViewController: UIViewController {

    
    @IBAction func setCookieThroughApi(_ sender: Any) {
        let request = URLRequest(url: URL.localApiServer, cachePolicy: .useProtocolCachePolicy, timeoutInterval: 10)
        let session = URLSession.shared
        session.configuration.httpCookieAcceptPolicy = .always
        session.configuration.httpCookieStorage = HTTPCookieStorage.shared
        session.configuration.httpShouldSetCookies = true
        let dataTask = session.dataTask(with: request) { (data, response, error) in
            if error == nil {
              print("Success fetch!")
              guard
                  let url = response?.url,
                  let httpResponse = response as? HTTPURLResponse,
                  let fields = httpResponse.allHeaderFields as? [String: String]
              else { return }
              let cookies = HTTPCookie.cookies(withResponseHeaderFields: fields, for: url)
              HTTPCookieStorage.shared.setCookies(cookies, for: url, mainDocumentURL: nil)
              for cookie in cookies {
                  var cookieProperties = [HTTPCookiePropertyKey: Any]()
                  cookieProperties[.name] = cookie.name
                  cookieProperties[.value] = cookie.value
                  cookieProperties[.domain] = cookie.domain
                  cookieProperties[.path] = cookie.path
                  cookieProperties[.version] = cookie.version
                  cookieProperties[.expires] = Date().addingTimeInterval(31536000)
                  let newCookie = HTTPCookie(properties: cookieProperties)


                  // This line is not needed, as the cookies are from HTTPCookieStorage.shared
                  // HTTPCookieStorage.shared.setCookie(newCookie!)
                  DispatchQueue.main.async {
                    WKWebsiteDataStore.default().httpCookieStore.setCookie(newCookie!, completionHandler: nil)
                  }
                  print("Api Set Cookie name: \(cookie.name) value: \(cookie.value)")
              }

            } else {
              print("Ops! \(error.debugDescription)")
            }
        }
        dataTask.resume()
    }
    
    @IBAction func setCookieThroughApp(_ sender: Any) {
        let cookieName = "native-cookie-set-key"
        let cookieValue = "this-is-set-by-native"
        var cookieProperties = [HTTPCookiePropertyKey: Any]()
        cookieProperties[.name] = cookieName
        cookieProperties[.value] = cookieValue
        cookieProperties[.domain] = "127.0.0.1"
        cookieProperties[.path] = ""
        cookieProperties[.expires] = Date().addingTimeInterval(31536000)
        let newCookie = HTTPCookie(properties: cookieProperties)
        WKWebsiteDataStore.default().httpCookieStore.setCookie(newCookie!, completionHandler: {
            print("App Set Cookie name: \(cookieName) value: \(cookieValue)")
        })
    }
    
    @IBAction func setCookieThroughWebView(_ sender: Any) {
        let storyboard = UIStoryboard(name: "Main", bundle: nil)
        if let vc = storyboard.instantiateViewController(withIdentifier: "webviewController") as? WebViewController {
            vc.url = URL.localWebCookieSetter
            self.present(vc, animated: true)
        }
    }
    
    @IBAction func openWebView(_ sender: Any) {
        let storyboard = UIStoryboard(name: "Main", bundle: nil)
        if let vc = storyboard.instantiateViewController(withIdentifier: "webviewController") as? WebViewController {
            vc.url = URL.localWebServer
            self.present(vc, animated: true)
        }
    }
    
    @IBAction func openSafariViewController(_ sender: Any) {
        let safari = SFSafariViewController(url: URL.localWebServer)
        self.present(safari, animated: true, completion: nil)
    }
    
    @IBAction func clearCookies(_ sender: Any) {
        WKWebsiteDataStore.default().removeData(
            ofTypes: WKWebsiteDataStore.allWebsiteDataTypes(),
            modifiedSince: Date(timeIntervalSince1970: 0),
            completionHandler: {})
        
        HTTPCookieStorage.shared.removeCookies(since: Date(timeIntervalSince1970: 0))
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
    }
}

