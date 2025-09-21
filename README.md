# Single-URL Crawler + Classifier

A modular service that fetches a URL, extracts metadata and main content, classifies the page (e.g., product, news, blog, category, unknown), and proposes topics. The project is divided into two modules:

1. **crawler-core-engine**: Core logic for fetching, extracting, and processing web pages.
2. **crawler-rest-api**: REST API layer to expose the functionality of the core engine.

---

## Modules

### 1. crawler-core-engine
This module contains the core functionality of the crawler, including:
- **Fetching**: Uses `HttpClient` to fetch web pages with support for gzip decoding and custom timeouts.
- **Robots.txt Compliance**: Ensures politeness by checking `robots.txt` rules before fetching.
- **Extraction**: Extracts metadata, main content, and links using Jsoup-based heuristics.
- **Classification**: Classifies the page type (e.g., product, news, blog) using heuristic signals.
- **Topic Extraction**: Proposes topics based on n-gram frequency analysis.

#### Key Features
- **Politeness**: Respects `robots.txt` rules.
- **Error Handling**: Handles non-200 HTTP responses gracefully.
- **Extensibility**: Easy to swap out components like classification or topic extraction.

---

### 2. crawler-rest-api
This module provides a REST API to interact with the core engine for testing Purpose. It exposes the following endpoint:

#### API Endpoint
`curl --location 'localhost:8080/extract' \
--header 'Content-Type: application/json' \
--data '{
    "url":"http://www.cnn.com/2013/06/10/politics/edward-snowden-profile/"
}'`  
Processes a URL and returns metadata, content, and NLP analysis.

#### Request
```json
{
    "url":"http://www.cnn.com/2013/06/10/politics/edward-snowden-profile/"
}
```

#### Response
```json
{
    "inputUrl": "http://www.cnn.com/2013/06/10/politics/edward-snowden-profile/",
    "urlFinal": "https://www.cnn.com/2013/06/10/politics/edward-snowden-profile/",
    "fetchedAt": "2025-09-21T07:58:42.297146Z",
    "statusCode": 200,
    "metadata": {
        "title": "Man behind NSA leaks says he did it to safeguard privacy, liberty | CNN Politics",
        "description": "Edward Snowden might never live in the U.S. as a free man again after leaking secrets about a U.S. surveillance program",
        "canonical": "https://www.cnn.com/2013/06/10/politics/edward-snowden-profile",
        "lang": "en",
        "og": {
            "og:title": "Man behind NSA leaks says he did it to safeguard privacy, liberty | CNN Politics",
            "twitter:title": "Man behind NSA leaks says he did it to safeguard privacy, liberty | CNN Politics",
            "twitter:description": "Edward Snowden might never live in the U.S. as a free man again after leaking secrets about a U.S. surveillance program",
            "twitter:image": "https://media.cnn.com/api/v1/images/stellar/prod/130609164540-edward-snowden-guardian.jpg?q=x_0,y_0,h_1440,w_2560,c_fill/w_800",
            "og:image": "https://media.cnn.com/api/v1/images/stellar/prod/130609164540-edward-snowden-guardian.jpg?q=x_0,y_0,h_1440,w_2560,c_fill/w_800",
            "og:type": "article",
            "twitter:card": "summary_large_image",
            "og:url": "https://www.cnn.com/2013/06/10/politics/edward-snowden-profile",
            "twitter:site": "@cnnpolitics",
            "og:site_name": "CNN"
        },
        "publishedTime": "2013-06-10T08:30:15Z"
    },
    "content": {
        "text": "Story highlights\n\nUnclear where Snowden will wind up, after leaving Hong Kong for Russia\n\nEdward Snowden, 29, is the source of leaks over an NSA surveillance program\n\n\"The public needs to decide whether these programs ... are right or wrong,\" he says\n\nHe’s a high school dropout who worked his way into the most secretive computers in U.S. intelligence as a defense contractor – only to blow those secrets wide open by spilling details of classified surveillance programs.\n\nNow, Edward Snowden might never live in the United States as a free man again. Where he may end up was a source of global speculation Sunday after he flew from Hong Kong to Russia, his ultimate destination unknown to most.\n\nSnowden has revealed himself as the source of documents outlining a massive effort by the U.S. National Security Agency to track cell phone calls and monitor the e-mail and Internet traffic of virtually all Americans.\n\nSnowden, 29, said he just wanted the public to know what the government was doing.\n\n“Even if you’re not doing anything wrong you’re being watched and recorded,” he said.\n\nRelated video Legal risks for NSA whistleblower\n\nRelated video Pentagon Papers Whistleblower on NSA\n\nRelated video Obama answers outcry over NSA\n\nSnowden told The Guardian newspaper in the United Kingdom that he had access to the full rosters of everyone working at the NSA, the entire intelligence community and undercover assets around the world.\n\n“I’m just another guy who sits there day to day in the office, watching what’s happening, and goes, ‘This is something that’s not our place to decide.’ The public needs to decide whether these programs or policies are right or wrong,” he said.\n\nSnowden fled to Hong Kong after copying one last set of documents and telling his boss he needed to go away for medical treatment.\n\nBefore his leak of U.S. intelligence, Snowden was living “in paradise.”\n\nHe worked for a major U.S. government contractor in Hawaii, earning a six-figure salary and enjoying the scenic state with his girlfriend.\n\nHe told The Guardian he never received a high school diploma and didn’t complete his computer studies at a community college. Instead, he joined the Army in 2003 but was discharged after breaking both legs in an accident.\n\nSnowden said he later worked as a security guard for the NSA and then took a computer security job with the CIA. He left that job in 2009 and moved on to Booz Allen Hamilton, where he worked as a contractor for the government in Hawaii.\n\nHe told the Guardian that he left for Hong Kong on May 20 without telling his family or his girlfriend what he planned.\n\nRelated video Patriot Act at center of NSA controversy\n\nRelated video Sanders: I voted against the Patriot Act\n\n“You’re living in Hawaii, in paradise and making a ton of money. What would it take to make you leave everything behind?” he said in the Guardian interview.\n\n“I’m willing to sacrifice all of that because I can’t in good conscience allow the U.S. government to destroy privacy, Internet freedom and basic liberties for people around the world with this massive surveillance machine they’re secretly building.”\n\nSome residents on Oahu island are glad Snowden left.\n\n“From a Hawaii standpoint, good riddance, thanks for leaving,” Ralph Cossa told CNN affiliate KHON.\n\n“I’m sure the guy had an overactive Mother Teresa gene and thought he was going to go out and save America from Americans, but in reality he was very foolish,” Cossa said. “We expect the government to honor our privacy, but we also expect our government to protect us from terrorist attacks.”\n\nPresident Barack Obama insists his administration is not spying on U.S. citizens – rather, it’s only looking for information on terrorists.\n\nBooz Allen Hamilton, the government contractor that employed Snowden, said Snowden had worked at the firm for less than three months.\n\n“News reports that this individual has claimed to have leaked classified information are shocking, and if accurate, this action represents a grave violation of the code of conduct and core values of our firm,” the company said in the statement. The firm said it will cooperate with authorities in their investigation.\n\nAccording to the Guardian, the only time Snowden became emotional during hours of interviews was when he thought about what might happen to his relatives – many of whom work for the U.S. government.\n\n“The only thing I fear is the harmful effects on my family, who I won’t be able to help anymore,” he said. “That’s what keeps me up at night.”\n\nAs for his concerns about his country, “the greatest fear that I have regarding the outcome for America of these disclosures is that nothing will change.”\n\nREAD: Some shrug at NSA snooping: Privacy’s already dead\n\nOfficial: Damage assessment over U.S. intelligence-gathering leaks\n\nCNNs Matt Smith and Holly Yan contributed to this report.",
        "images": [
            {
                "src": "https://media.cnn.com/api/v1/images/stellar/prod/130609163333-nr-desjardins-nsa-leaker-the-guardian-00003419.jpg?q=x_0,y_0,h_604,w_1075,c_fill/w_1280",
                "alt": "nr desjardins nsa leaker the guardian _00003419.jpg"
            },
            {
                "src": "https://media.cnn.com/api/v1/images/stellar/prod/130609225848-baer-nsa-whistleblower-00001115.jpg?q=x_174,y_0,h_622,w_1105,c_crop/h_144,w_256",
                "alt": "baer.nsa.whistleblower _00001115.jpg"
            },
            {
                "src": "https://media.cnn.com/api/v1/images/stellar/prod/130609220414-exp-pentagon-papers-whistleblower-on-nsa-00021027.jpg?q=x_0,y_0,h_720,w_1280,c_fill/h_144,w_256",
                "alt": "exp Pentagon Papers Whistleblower on NSA_00021027.jpg"
            },
            {
                "src": "https://media.cnn.com/api/v1/images/stellar/prod/130607154832-exp-nr-nsa-ben-ferguson-00002001.jpg?q=x_0,y_0,h_720,w_1280,c_fill/h_144,w_256",
                "alt": "exp nr nsa ben ferguson_00002001.jpg"
            },
            {
                "src": "https://media.cnn.com/api/v1/images/stellar/prod/130606190619-tsr-johns-nsa-verizon-progam-leak-00024213.jpg?q=x_117,y_25,h_576,w_1024,c_crop/h_144,w_256",
                "alt": "tsr johns NSA verizon progam leak_00024213.jpg"
            },
            {
                "src": "https://media.cnn.com/api/v1/images/stellar/prod/130606215739-exp-pmt-bernie-sanders-patriot-act-nsa-00012623.jpg?q=x_143,y_26,h_576,w_1024,c_crop/h_144,w_256",
                "alt": "exp pmt bernie sanders patriot act nsa_00012623.jpg"
            }
        ],
        "links": [
            {
                "href": "https://www.cnn.com/",
                "rel": ""
            },
            {
                "href": "http://cnn.com/2013/06/09/politics/nsa-leak-identity/index.html",
                "rel": ""
            },
            {
                "href": "https://www.cnn.com/videos/world/2013/06/10/baer-nsa-whistleblower.cnn",
                "rel": ""
            },
            {
                "href": "https://www.cnn.com/videos/bestoftv/2013/06/10/exp-pentagon-papers-whistleblower-on-nsa.cnn",
                "rel": ""
            },
            {
                "href": "https://www.cnn.com/videos/bestoftv/2013/06/07/exp-nr-nsa-ben-ferguson.cnn",
                "rel": ""
            },
            {
                "href": "http://cnn.com/2013/06/10/politics/nsa-leak/index.html",
                "rel": ""
            },
            {
                "href": "https://www.cnn.com/videos/us/2013/06/06/tsr-johns-nsa-verizon-progam-leak.cnn",
                "rel": ""
            },
            {
                "href": "https://www.cnn.com/videos/bestoftv/2013/06/07/exp-pmt-bernie-sanders-patriot-act-nsa.cnn",
                "rel": ""
            },
            {
                "href": "http://www.khon2.com/2013/06/09/man-behind-nsa-leaks-worked-in-hawaii/",
                "rel": ""
            },
            {
                "href": "http://www.cnn.com/2013/06/10/opinion/rushkoff-snowden-hero/index.html",
                "rel": ""
            },
            {
                "href": "http://www.boozallen.com/media-center/press-releases/48399320/statement-reports-leaked-information-060913",
                "rel": ""
            },
            {
                "href": "http://cnn.com/2013/06/07/tech/web/nsa-internet-privacy",
                "rel": ""
            },
            {
                "href": "http://cnn.com/2013/06/07/politics/nsa-data-mining/index.html",
                "rel": ""
            },
            {
                "href": "http://cnn.com/2013/06/08/politics/nsa-data-mining/index.html",
                "rel": ""
            }
        ]
    },
    "nlp": {
        "pageType": "news_article",
        "topics": [
            "snowden",
            "nsa",
            "said",
            "government",
            "related video",
            "worked",
            "video",
            "what",
            "related",
            "guardian"
        ]
    }
}
```
---

## Quickstart (Local)
Prerequisites
- Java: 11
- Maven: 3.8.5

Build and Run
```# Build the project
mvn -q clean package

# Run the application
java -jar crawler-rest-api/target/crawler-rest-api-1.0.0.jar
```
---
## Containerization

Build Docker Image

```docker build -t single-url-crawler:1.0.0 .```

Run Docker Container

```docker run -p 8080:8080 single-url-crawler:1.0.0 ```