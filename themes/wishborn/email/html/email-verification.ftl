<#ftl output_format="HTML">
<#import "template.ftl" as layout>
<@layout.emailLayout>
    <!DOCTYPE html>
    <html lang="fr">
    <head>
        <meta charset="utf-8">
        <title>${msg("emailVerificationSubject")}</title>
        <style>
            body {
                font-family: 'Inter', Arial, sans-serif;
                background-color: #EEEEEE;
                margin: 0;
                padding: 20px;
                color: #4A4A4A;
            }
            .email-container {
                max-width: 600px;
                margin: 0 auto;
                background: #FFFFFF;
                border: 1px solid #E5E7EB;
                border-radius: 12px;
                box-shadow: 0 4px 6px rgba(0, 0, 0, 0.05);
                overflow: hidden;
            }
            .email-header {
                background-color: #6985C0;
                padding: 20px;
                text-align: center;
            }
            .email-logo {
                max-width: 220px;
                height: auto;
            }
            .email-content {
                padding: 30px;
            }

            /* ✅ This turns any <a> inside the .email-content into a styled button */
            .email-content a[href^="http"] {
                display: inline-block;
                background-color: #6985C0;
                color: #FFFFFF !important;
                text-decoration: none;
                font-weight: 600;
                padding: 12px 24px;
                border-radius: 8px;
                margin: 20px 0;
                text-align: center;
            }

            .email-footer {
                padding: 20px;
                text-align: center;
                font-size: 14px;
                color: #6B7280;
                background-color: #F9FAFB;
                border-top: 1px solid #E5E7EB;
            }
        </style>
    </head>
    <body>
    <div class="email-container">
        <div class="email-header">
        </div>

        <div class="email-content">
            ${kcSanitize(msg("emailVerificationBodyHtml", link, linkExpiration, realmName, linkExpirationFormatter(linkExpiration)))?no_esc}
        </div>

        <div class="email-footer">
            <p></p>
        </div>
    </div>
    </body>
    </html>
</@layout.emailLayout>
