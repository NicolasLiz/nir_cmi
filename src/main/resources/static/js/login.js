document.getElementById('loginForm').addEventListener('submit', async function(e) {
    e.preventDefault(); // Prevent default HTML page reload

    const email = document.getElementById('email').value;
    const password = document.getElementById('password').value;

    try {
        const response = await fetch('/api/auth/login', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                userEmail: email,
                userPassword: password
            })
        });

        if (response.ok) {
            window.location.href = response.url;
        } else {
            // Handle error response sent via response.sendError()
            let errorMessage = 'Login failed: Invalid email or password.';
            try {
                const text = await response.text();
                if (text) errorMessage = text;
            } catch (e) {
                // Ignore parse errors if response body is empty
            }
            alert(errorMessage);
        }
    } catch (error) {
        console.error('Error during login:', error);
        alert('Network or server connection error.');
    }
});
