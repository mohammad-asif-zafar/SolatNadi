// SolatNadi Website Interactive Script

document.addEventListener('DOMContentLoaded', () => {
    // 1. Mobile Menu Toggle
    const mobileToggle = document.getElementById('mobileNavToggle');
    const navLinks = document.getElementById('navLinks');

    if (mobileToggle && navLinks) {
        mobileToggle.addEventListener('click', () => {
            navLinks.classList.toggle('active');
            const isExpanded = navLinks.classList.contains('active');
            mobileToggle.setAttribute('aria-expanded', isExpanded);
        });
    }

    // 2. Contact Form Validation & Submission Feedback
    const contactForm = document.getElementById('contactForm');
    const formFeedback = document.getElementById('formFeedback');

    if (contactForm && formFeedback) {
        contactForm.addEventListener('submit', (e) => {
            e.preventDefault();

            const name = document.getElementById('senderName')?.value.trim();
            const email = document.getElementById('senderEmail')?.value.trim();
            const subject = document.getElementById('emailSubject')?.value.trim();
            const message = document.getElementById('emailMessage')?.value.trim();

            if (!name || !email || !message) {
                formFeedback.className = 'form-feedback error';
                formFeedback.textContent = 'Please fill in all required fields (Name, Email, Message).';
                return;
            }

            // Construct mailto link as direct safe integration option
            const mailtoSubject = encodeURIComponent(`[SolatNadi Support] ${subject || 'General Inquiry'}`);
            const mailtoBody = encodeURIComponent(`Name: ${name}\nEmail: ${email}\n\nMessage:\n${message}`);
            const mailtoUrl = `mailto:support@solatnadi.app?subject=${mailtoSubject}&body=${mailtoBody}`;

            formFeedback.className = 'form-feedback success';
            formFeedback.innerHTML = `Thank you, <strong>${name}</strong>! Opening your email client to send message to support@solatnadi.app...`;

            setTimeout(() => {
                window.location.href = mailtoUrl;
                contactForm.reset();
            }, 800);
        });
    }
});
