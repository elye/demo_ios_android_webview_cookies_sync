import UIKit
import WebKit

class WebViewController: UIViewController {

    @IBOutlet weak var webView: WKWebView!
    
    var url: URL = URL(string: "https://www.google.com")!;
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        // Manual cookies injection, ahd HTTPCookieStorage shared is not shared with Webview
//        let cookies = HTTPCookieStorage.shared.cookies ?? []
//        for (cookie) in cookies {
//            webView.configuration.websiteDataStore.httpCookieStore.setCookie(cookie)
//        }

        let request = URLRequest(url: url)
        webView.load(request)
    }

}
